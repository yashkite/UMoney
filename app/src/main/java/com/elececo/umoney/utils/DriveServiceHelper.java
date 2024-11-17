package com.elececo.umoney.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class DriveServiceHelper {
    private static final String TAG = "DriveServiceHelper";
    private final Drive driveService;
    private final Context context;
    private static final String APPLICATION_NAME = "UMoney";
    private final ExecutorService executor;
    private static final String ROOT_FOLDER_NAME = "UMoney";
    private static final String ATTACHMENTS_FOLDER_NAME = "Attachments";

    public DriveServiceHelper(Context context, GoogleSignInAccount account) throws Exception {
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
        
        if (!hasRequiredScopes(account)) {
            throw new Exception("Required Drive permissions not granted");
        }
        
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
            context,
            Collections.singleton(DriveScopes.DRIVE_FILE)
        );
        credential.setSelectedAccount(account.getAccount());
        
        driveService = new Drive.Builder(
            new NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
        .setApplicationName(APPLICATION_NAME)
        .build();
    }

    private boolean hasRequiredScopes(GoogleSignInAccount account) {
        return account.getGrantedScopes().contains(new Scope(DriveScopes.DRIVE_FILE));
    }

    public Task<String[]> uploadFile(Uri fileUri, String fileName, String transactionType) {
        return Tasks.call(executor, () -> {
            try {
                // Get or create the folder structure
                String rootFolderId = getOrCreateFolder(null, ROOT_FOLDER_NAME);
                String attachmentsFolderId = getOrCreateFolder(rootFolderId, ATTACHMENTS_FOLDER_NAME);
                String typeFolderId = getOrCreateFolder(attachmentsFolderId, transactionType);
                
                File fileMetadata = new File();
                fileMetadata.setName(fileName);
                fileMetadata.setParents(Collections.singletonList(typeFolderId));

                java.io.File tempFile = createTempFile(fileUri);
                FileContent mediaContent = new FileContent(
                    context.getContentResolver().getType(fileUri),
                    tempFile
                );

                File file = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, name")
                    .execute();
                    
                tempFile.delete();
                return new String[]{file.getId(), file.getName()};
            } catch (IOException e) {
                Log.e(TAG, "Error uploading file: " + e.getMessage());
                throw e;
            }
        });
    }

    private String getOrCreateFolder(String parentFolderId, String folderName) throws IOException {
        String query = "name='" + folderName + "' and mimeType='application/vnd.google-apps.folder'";
        if (parentFolderId != null) {
            query += " and '" + parentFolderId + "' in parents";
        }
        
        File folder = driveService.files().list()
            .setQ(query)
            .setSpaces("drive")
            .execute()
            .getFiles()
            .stream()
            .findFirst()
            .orElse(null);
            
        if (folder != null) {
            return folder.getId();
        }
        
        // Create new folder
        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");
        if (parentFolderId != null) {
            folderMetadata.setParents(Collections.singletonList(parentFolderId));
        }
        
        folder = driveService.files().create(folderMetadata)
            .setFields("id")
            .execute();
            
        return folder.getId();
    }

    public Task<Void> deleteFile(String fileId) {
        return Tasks.call(executor, () -> {
            driveService.files().delete(fileId).execute();
            return null;
        });
    }

    public void openFile(Context context, String fileId) {
        Tasks.call(executor, () -> {
            File file = driveService.files().get(fileId)
                .setFields("webViewLink")
                .execute();
                
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(file.getWebViewLink()));
            context.startActivity(intent);
            return null;
        });
    }

    private java.io.File createTempFile(Uri uri) throws IOException {
        java.io.File tempFile = java.io.File.createTempFile("temp", null, context.getCacheDir());
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new java.io.FileOutputStream(tempFile)) {
            if (inputStream == null) {
                throw new IOException("Failed to open input stream");
            }
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (Exception e) {
            tempFile.delete();
            throw new IOException("Failed to copy file: " + e.getMessage());
        }
        return tempFile;
    }
} 