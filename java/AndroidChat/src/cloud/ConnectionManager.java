package cloud;

import java.util.ArrayList;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import window.ChatWindow;

public class ConnectionManager {
	private static final String FIREBASE_URL = "https://fiery-inferno-5459.firebaseio.com";
	
	private Firebase mFirebaseRef;
	private ValueEventListener mConnectedListener;
	
	private Query mRef;
	private Class<Chat> mModelClass;
	private ArrayList<Chat> mModels;
	private ArrayList<String> mKeys;
	private ChildEventListener mListener;
	
	private ChatWindow chatWindow;
	
	public ConnectionManager(ChatWindow chatWindow) {
		mFirebaseRef = new Firebase(FIREBASE_URL).child("chat");
		mRef = mFirebaseRef.limitToFirst(50);
		
		mModelClass = Chat.class;
		mModels = new ArrayList<Chat>();
		mKeys = new ArrayList<String>();
		
		checkConnection();
		setRetrieveData();
		
		this.chatWindow = chatWindow;
	}
	
	private void checkConnection() {
			mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				 boolean connected = (Boolean) dataSnapshot.getValue();
				 if(connected)
					 System.out.println("Connected to Firebase");
				 else
					 System.out.println("Disconnected from Firebase");
			}
			
			@Override
			public void onCancelled(FirebaseError firebaseError) {
				// No-op
			}
		});
	}
	
	private void setRetrieveData() {
		mListener = mRef.addChildEventListener(new ChildEventListener() {
			
			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				String key = dataSnapshot.getKey();
				int index = mKeys.indexOf(key);
				
				mKeys.remove(index);
				mModels.remove(index);
				
				chatWindow.populateView(mModels, mKeys);
			}
			
			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
				String key = dataSnapshot.getKey();
                Chat newModel = dataSnapshot.getValue(mModelClass);
                int index = mKeys.indexOf(key);
                mModels.remove(index);
                mKeys.remove(index);
                if (previousChildName == null) {
                    mModels.add(0, newModel);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(newModel);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, newModel);
                        mKeys.add(nextIndex, key);
                    }
                }
                
                chatWindow.populateView(mModels, mKeys);
			}
			
			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				Chat newModel = dataSnapshot.getValue(mModelClass);
				String key = dataSnapshot.getKey();
				int index = mKeys.indexOf(key);
                mModels.set(index, newModel);
                
                chatWindow.populateView(mModels, mKeys);
			}
			
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
				Chat model = dataSnapshot.getValue(mModelClass);
				String key = dataSnapshot.getKey();
				
				if (previousChildName == null) {
                    mModels.add(0, model);
                    mKeys.add(0, key);
                } else {
                    int previousIndex = mKeys.indexOf(previousChildName);
                    int nextIndex = previousIndex + 1;
                    if (nextIndex == mModels.size()) {
                        mModels.add(model);
                        mKeys.add(key);
                    } else {
                        mModels.add(nextIndex, model);
                        mKeys.add(nextIndex, key);
                    }
                }
				
				chatWindow.populateView(mModels, mKeys);
			}
			
			@Override
			public void onCancelled(FirebaseError firebaseError) {
				//"FirebaseListAdapter", "Listen was cancelled, no more updates will occur"
			}
		});
	}
	
	public void sendMessage(String input) {
		if(!input.equals("")){
			Chat chat = new Chat(input, "DesktopUser");
			mFirebaseRef.push().setValue(chat);
		}
	}
	
	public void cleanUp() {
		mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
		mRef.removeEventListener(mListener);
        mModels.clear();
        mKeys.clear();
	}
}
