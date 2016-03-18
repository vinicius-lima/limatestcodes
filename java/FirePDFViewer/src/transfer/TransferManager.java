package transfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.CountDownLatch;

import javax.xml.bind.DatatypeConverter;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.Firebase.CompletionListener;

import main.BaseViewerFX;

public class TransferManager {

	public static final String FIREBASE_URL = "https://fiery-inferno-5459.firebaseio.com";

	private BaseViewerFX viewer;

	private String user_name;
	private Firebase mFirebaseRef;
	private ValueEventListener mConnectedListener;
	private ValueEventListener mListener;

	public TransferManager(String user_name, BaseViewerFX vw) {
		this.user_name = user_name;
		viewer = vw;

		// Connect to Firebase service.
		mFirebaseRef = new Firebase(FIREBASE_URL).child("pdfviewer/" + this.user_name);

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
				TransferWrapper dataDescription = dataSnapshot.getValue(TransferWrapper.class);
				if (dataDescription != null) {
					byte[] content = DatatypeConverter.parseBase64Binary(dataDescription.getFileContent());
					saveFileContent(dataDescription.getPDFfile(), content);
					viewer.resumeExecution(dataDescription);
				}
			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {
				System.out.println("Could not retrieve data");
			}
		});
	}

	public void cleanUp() {
		mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
		mFirebaseRef.removeEventListener(mListener);
	}

	public void saveFileContent(String name, byte[] fileContent) {
		try {
			File file = new File(name);
			if(file.exists())
				file.delete();

			DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
			out.write(fileContent);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void transfer(TransferWrapper wrapper) {
		try {
			File file = new File(wrapper.getPDFfile());
			wrapper.setPDFfile(file.getName());

			// Reads file content.
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			byte[] content = new byte[in.available()];
			in.readFully(content);
			wrapper.setFileContent(DatatypeConverter.printBase64Binary(content));
			in.close();

			final CountDownLatch done = new CountDownLatch(1);
			//System.out.println("Sending state...");
			mFirebaseRef.setValue(wrapper, new CompletionListener() {

				@Override
				public void onComplete(FirebaseError firebaseError, Firebase firebase) {
					done.countDown();
				}
			});
			try {
				done.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
