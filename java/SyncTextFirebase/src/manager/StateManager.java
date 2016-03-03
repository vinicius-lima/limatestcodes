package manager;

import java.util.concurrent.CountDownLatch;

import javax.xml.bind.DatatypeConverter;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.CompletionListener;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import window.EditionPanel;

public class StateManager {
	
	public static final String FIREBASE_URL = "https://fiery-inferno-5459.firebaseio.com";
	
	private EditionPanel editionPanel;
	private String user_name;
	
	private Firebase mFirebaseRef;
	private ValueEventListener mConnectedListener;
	private ValueEventListener mListener;
	
	private Firebase ackFirebaseRef;
	private ValueEventListener ackListener;
	private final LatencyStatus latencyStatus;
	
	public StateManager(EditionPanel editionPanel) {
		this.editionPanel = editionPanel;
		user_name = null;
		latencyStatus = new LatencyStatus();
	}
	
	public void startStateManager(String user_name) {
		this.user_name = user_name;
		
		// Connect to Firebase service.
        mFirebaseRef = new Firebase(FIREBASE_URL).child("synctext/" + this.user_name);
        ackFirebaseRef = new Firebase(FIREBASE_URL).child("synctext/ack");

        // Check if our app is connected.
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean connected = (Boolean) dataSnapshot.getValue();
                if (connected) {
                    System.out.println("Connected to Firebase");
                } else {
                    System.out.println("Disconnected from Firebase");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // No-op
            }
        });

        // Set up retrieving data options.
        mListener = mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataDescription dataDescription = dataSnapshot.getValue(DataDescription.class);
                if (dataDescription != null) {
                    String text = new String(DatatypeConverter.parseBase64Binary(dataDescription.getData64Encoded()));
                    editionPanel.setText(text);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("Could not retrieve data");
            }
        });
        ackListener = ackFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long time = (long)dataSnapshot.getValue();
                if (time != 0L) {
                    latencyStatus.setFinalTime(System.currentTimeMillis());
                    System.out.println("Latency = " + latencyStatus.getDifference());
                    latencyStatus.clear();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("Could not retrieve ACK");
            }
        });
	}
	
	public void cleanUp() {
		mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
		mFirebaseRef.removeEventListener(mListener);
		ackFirebaseRef.removeEventListener(ackListener);
	}
	
	public void transferState() {
        byte[] data = editionPanel.getText().getBytes();
        DataDescription dataDescription = new DataDescription("save.txt", data.length, DatatypeConverter.printBase64Binary(data));
        
        final CountDownLatch done = new CountDownLatch(1);
        latencyStatus.setInitialTime(System.currentTimeMillis());
        System.out.println("Sending state...");
        mFirebaseRef.setValue(dataDescription, new CompletionListener() {
			
			@Override
			public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                done.countDown();
			}
		});
        try {
			done.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
