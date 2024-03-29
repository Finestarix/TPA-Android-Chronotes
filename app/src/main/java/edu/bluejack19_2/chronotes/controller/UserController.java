package edu.bluejack19_2.chronotes.controller;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.bluejack19_2.chronotes.interfaces.BytesListener;
import edu.bluejack19_2.chronotes.interfaces.ProcessStatusListener;
import edu.bluejack19_2.chronotes.interfaces.UserListener;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public class UserController {

    private static UserController userController;

    private StorageReference storageReference;
    private CollectionReference collectionReference;

    private ProcessStatus currentStatus;

    private UserController() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(User.COLLECTION_NAME);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("users");
    }

    public static UserController getInstance() {
        if (userController == null) {
            userController = new UserController();
        }
        return userController;
    }

    public void getUserByID(UserListener userListener, String id) {

        collectionReference.
                whereEqualTo("id", id).
                get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (Objects.requireNonNull(task.getResult()).isEmpty())
                            userListener.onCallback(null, ProcessStatus.NOT_FOUND);
                        else {
                            QueryDocumentSnapshot queryDocumentSnapshot = Objects.requireNonNull(task.getResult()).iterator().next();

                            Map<String, Object> userData = queryDocumentSnapshot.getData();

                            String idUserData = Objects.requireNonNull(userData.get("id")).toString();
                            String nameUserData = Objects.requireNonNull(userData.get("name")).toString();
                            String emailUserData = Objects.requireNonNull(userData.get("email")).toString();
                            String passwordUserData = Objects.requireNonNull(userData.get("password")).toString();
                            String pictureUserData = Objects.requireNonNull(userData.get("picture")).toString();

                            User user = new User(idUserData, nameUserData, emailUserData, passwordUserData, pictureUserData);

                            userListener.onCallback(user, ProcessStatus.FOUND);
                        }
                    } else
                        userListener.onCallback(null, ProcessStatus.FAILED);
                });
    }

    public void getUserByEmail(UserListener userListener, String email) {

        collectionReference.
                whereEqualTo("email", email).
                get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (Objects.requireNonNull(task.getResult()).isEmpty())
                            userListener.onCallback(null, ProcessStatus.NOT_FOUND);
                        else {
                            QueryDocumentSnapshot queryDocumentSnapshot = Objects.requireNonNull(task.getResult()).iterator().next();

                            Map<String, Object> userData = queryDocumentSnapshot.getData();

                            String idUserData = Objects.requireNonNull(userData.get("id")).toString();
                            String nameUserData = Objects.requireNonNull(userData.get("name")).toString();
                            String emailUserData = Objects.requireNonNull(userData.get("email")).toString();
                            String passwordUserData = Objects.requireNonNull(userData.get("password")).toString();
                            String pictureUserData = Objects.requireNonNull(userData.get("picture")).toString();

                            User user = new User(idUserData, nameUserData, emailUserData, passwordUserData, pictureUserData);

                            userListener.onCallback(user, ProcessStatus.FOUND);
                        }
                    } else
                        userListener.onCallback(null, ProcessStatus.FAILED);
                });
    }

    public void findEmail(ProcessStatusListener processStatusListener, String email) {
        currentStatus = ProcessStatus.INIT;

        collectionReference.
                whereEqualTo("email", email).
                get().
                addOnCompleteListener(task -> {
                    currentStatus = (task.isSuccessful()) ?
                            (!Objects.requireNonNull(task.getResult()).iterator().hasNext()) ?
                                    ProcessStatus.NOT_FOUND : ProcessStatus.FOUND : ProcessStatus.FAILED;
                    processStatusListener.onCallback(currentStatus);
                });
    }

    public void getUserPicture(BytesListener bytesListener, String picture) {
        storageReference.
                child(picture).
                getBytes(Long.MAX_VALUE).
                addOnCompleteListener(task -> bytesListener.onCallback(
                        (task.isSuccessful()) ? task.getResult() : null,
                        (task.isSuccessful()) ? ProcessStatus.SUCCESS : ProcessStatus.FAILED));
    }

    public void insertNewUser(ProcessStatusListener processStatusListener, User user) {
        currentStatus = ProcessStatus.INIT;

        Map<String, Object> userData = new HashMap<>();
        userData.put("id", user.getId());
        userData.put("name", user.getName());
        userData.put("email", user.getEmail());
        userData.put("password", user.getPassword());
        userData.put("picture", user.getPicture());

        collectionReference.
                document(User.DOCUMENT_NAME + user.getId()).
                set(userData).
                addOnCompleteListener(task -> {
                    currentStatus = (task.isComplete()) ?
                            ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                    processStatusListener.onCallback(currentStatus);
                });
    }

    public void uploadPhoto(UserListener userListener, String pictureID, Uri uri) {
        storageReference.
                child(pictureID).
                putFile(uri).
                addOnSuccessListener(taskSnapshot -> userListener.onCallback(null, ProcessStatus.SUCCESS)).
                addOnFailureListener(e -> userListener.onCallback(null, ProcessStatus.FAILED));
    }

    public void updateUserByID(ProcessStatusListener processStatusListener, User user, int index) {

        DocumentReference documentReference = collectionReference.document(User.DOCUMENT_NAME + user.getId());

        if (index == 0)
            documentReference.
                    update("name", user.getName()).
                    addOnCompleteListener(task -> {
                        currentStatus = (task.isComplete()) ?
                                ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                        processStatusListener.onCallback(currentStatus);
                    });

        else if (index == 1)
            documentReference.
                    update("name", user.getName(),
                            "picture", user.getPicture()).
                    addOnCompleteListener(task -> {
                        currentStatus = (task.isComplete()) ?
                                ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                        processStatusListener.onCallback(currentStatus);
                    });

        else if (index == 2)
            documentReference.
                    update("name", user.getName(),
                            "password", user.getPassword()).
                    addOnCompleteListener(task -> {
                        currentStatus = (task.isComplete()) ?
                                ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                        processStatusListener.onCallback(currentStatus);
                    });

        else if (index == 3)
            documentReference.
                    update("name", user.getName(),
                            "password", user.getPassword(),
                            "picture", user.getPicture()).
                    addOnCompleteListener(task -> {
                        currentStatus = (task.isComplete()) ?
                                ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                        processStatusListener.onCallback(currentStatus);
                    });
    }
}
