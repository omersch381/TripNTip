//package com.example.TripNTip.backup;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.util.Log;
//import android.widget.LinearLayout;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.storage.FileDownloadTask;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.ListResult;
//import com.google.firebase.storage.StorageReference;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//
//public class TripsPicturesAlbum {
//
//    private static TripsPicturesAlbum albumInstance = null;
//
//    private FirebaseStorage storage = FirebaseStorage.getInstance();
//
//    private StorageReference listRef = storage.getReference().getRoot().child("Images");
//
//    private ArrayList<Picture> album;
//
//    private Drawable tempPicture;
//
//    private TripsPicturesAlbum(Context context) {
//        album = new ArrayList<>();
//        addPicturesToTheAlbum(context);
//    }
//
//
//    public static TripsPicturesAlbum getInstance(Context context) {
//        if (albumInstance == null)
//            albumInstance = new TripsPicturesAlbum(context);
//
//        return albumInstance;
//    }
//
//
//    private void addPicturesToTheAlbum(final Context context) {
//        listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//            @Override
//            public void onSuccess(ListResult listResult) {
//                for (StorageReference item : listResult.getItems()) {
//                    try {
//                        Log.d("PRINT_PATH", item.getPath());
//                        LinearLayout linearLayout = new LinearLayout(context);
//                        Drawable pictureView = loadImageWithCreatingALocalFile(linearLayout, item, context);
//                        album.add(new Picture(item.getName(), pictureView));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // Uh-oh, an error occurred!
//            }
//        });
//    }
//
//    private Drawable loadImageWithCreatingALocalFile(final LinearLayout linearLayout, StorageReference storageReference, final Context context) throws IOException {
//        final File myImage = File.createTempFile("images", "jpg");
//        // We can switch the StorageReference storageReference to String url as well
////        StorageReference storageReference = storage.getReferenceFromUrl(url);
//        storageReference.getFile(myImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(myImage.getAbsolutePath());
//                tempPicture = new BitmapDrawable(context.getResources(), myBitmap);
//                linearLayout.setBackground(tempPicture);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.d("FAILURE_MSG", "Somehow there is a failure...");
//            }
//        });
//        return tempPicture;
//    }
//
//    public ArrayList<Picture> getAlbum() {
//        return album;
//    }
//}
