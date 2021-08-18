package automatic.learning.bookscanner;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dynamsoft.dbr.BarcodeReader;
import com.dynamsoft.dbr.BarcodeReaderException;
import com.dynamsoft.dbr.DBRLTSLicenseVerificationListener;
import com.dynamsoft.dbr.DMLTSConnectionParameters;
import com.dynamsoft.dbr.EnumImagePixelFormat;
import com.dynamsoft.dbr.TextResult;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private TextView resultView;
    private ExecutorService exec;
    private Camera camera;
    private BarcodeReader dbr;
    private String barcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
         previewView = findViewById(R.id.previewView);
        resultView = findViewById(R.id.resultView);
        exec = Executors.newSingleThreadExecutor();
        try {
            dbr = new BarcodeReader();
        } catch (BarcodeReaderException e) {
            e.printStackTrace();
        }
        DMLTSConnectionParameters parameters = new DMLTSConnectionParameters();
        parameters.organizationID = "100610907";
        dbr.initLicenseFromLTS(parameters, new DBRLTSLicenseVerificationListener() {
            @Override
            public void LTSLicenseVerificationCallback(boolean isSuccess, Exception error) {
                if (!isSuccess) {
                    error.printStackTrace();
                }
            }
        });
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreviewAndImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));

        Button submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send barcode to Books api
               // barcode = barcode.replace("*","");
                getBooksInfo(barcode);


            }
        });
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private void bindPreviewAndImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        Size resolution = new Size(720, 1280);
        Display d = getDisplay();
        if (d.getRotation() != Surface.ROTATION_0) {
            resolution = new Size(1280, 720);
        }

        Preview.Builder previewBuilder = new Preview.Builder();
        previewBuilder.setTargetResolution(resolution);
        Preview preview = previewBuilder.build();

        ImageAnalysis.Builder imageAnalysisBuilder = new ImageAnalysis.Builder();

        imageAnalysisBuilder.setTargetResolution(resolution)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST);

        ImageAnalysis imageAnalysis = imageAnalysisBuilder.build();

        imageAnalysis.setAnalyzer(exec, new ImageAnalysis.Analyzer() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void analyze(@NonNull ImageProxy image) {
                int rotationDegrees = image.getImageInfo().getRotationDegrees();
                TextResult[] results = null;
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                int nRowStride = image.getPlanes()[0].getRowStride();
                int nPixelStride = image.getPlanes()[0].getPixelStride();
                int length = buffer.remaining();
                byte[] bytes = new byte[length];
                buffer.get(bytes);
                ImageData imageData = new ImageData(bytes, image.getWidth(), image.getHeight(), nRowStride * nPixelStride);
                try {
                    results = dbr.decodeBuffer(imageData.mBytes, imageData.mWidth, imageData.mHeight, imageData.mStride, EnumImagePixelFormat.IPF_NV21, "");
                } catch (BarcodeReaderException e) {
                    e.printStackTrace();
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Found ").append(results.length).append(" barcode(s):\n");
                for (int i = 0; i < results.length; i++) {
                    barcode = results[0].barcodeText;
                    sb.append(results[i].barcodeText);
                    sb.append("\n");
                }

                Log.d("DBR", sb.toString());
                resultView.setText(sb.toString());
                image.close();
            }
        });

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        UseCaseGroup useCaseGroup = new UseCaseGroup.Builder()
                .addUseCase(preview)
                .addUseCase(imageAnalysis)
                .build();
        camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, useCaseGroup);
    }


    private void getBooksInfo(String query) {

        // creating a new array list.
        ArrayList<Object> bookInfoArrayList = new ArrayList<>();

        // below line is use to initialize
        // the variable for our request queue.
        RequestQueue mRequestQueue = Volley.newRequestQueue(CameraActivity.this);

        // below line is use to clear cache this
        // will be use when our data is being updated.
        mRequestQueue.getCache().clear();

        // below is the url for getting data from API in json format.
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + query;

        // below line we are  creating a new request queue.
        RequestQueue queue = Volley.newRequestQueue(CameraActivity.this);


        // below line is use to make json object request inside that we
        // are passing url, get method and getting json object. .
        JsonObjectRequest booksObjrequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // inside on response method we are extracting all our json data.
                try {
                    JSONArray itemsArray = response.getJSONArray("items");
                    Log.d("json", String.valueOf(itemsArray));
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemsObj = itemsArray.getJSONObject(i);
                        JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
                        String title = volumeObj.optString("title");
                        String subtitle = volumeObj.optString("subtitle");
                        JSONArray authorsArray = volumeObj.getJSONArray("authors");
                        String publisher = volumeObj.optString("publisher");
                        String publishedDate = volumeObj.optString("publishedDate");
                        String description = volumeObj.optString("description");
                        int pageCount = volumeObj.optInt("pageCount");
                        JSONObject imageLinks = volumeObj.optJSONObject("imageLinks");
                        String thumbnail = imageLinks.optString("thumbnail");
                        String previewLink = volumeObj.optString("previewLink");
                        String infoLink = volumeObj.optString("infoLink");
                        JSONObject saleInfoObj = itemsObj.optJSONObject("saleInfo");
                        String buyLink = saleInfoObj.optString("buyLink");
                        JSONArray isbn = volumeObj.getJSONArray("industryIdentifiers");
                        String isbn10 = isbn.getJSONObject(0).getString("identifier");
                        String isbn13 = isbn.getJSONObject(1).getString("identifier");
                        isbn10 = isbn10.replace("\"", "");
                        isbn13 = isbn13.replace("\"", "");
                        ArrayList<String> authorsArrayList = new ArrayList<>();
                        if (authorsArray.length() != 0) {
                            for (int j = 0; j < authorsArray.length(); j++) {
                                authorsArrayList.add(authorsArray.optString(i));
                            }
                        }
                        // after extracting all the data we are
                        // saving this data in our modal class.
                        Book bookInfo = new Book(Float.parseFloat(isbn10), Float.parseFloat(isbn13),
                                isbn10, title,
                                authorsArrayList.get(0), publisher,
                                publishedDate, (double)pageCount, thumbnail, description, buyLink);
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        FirebaseAuth inst = FirebaseAuth.getInstance();

                        mDatabase.child(inst.getCurrentUser().getEmail().replace(".","")).child((isbn10)).setValue(bookInfo);
                        // below line is use to pass our modal
                        // class in our array list.
                       // bookInfoArrayList.add(bookInfo);

                        // below line is use to pass our
                        // array list in adapter class.
                      //  BookAdapter adapter = new BookAdapter(bookInfoArrayList, MainActivity.this);

                        // below line is use to add linear layout
                        // manager for our recycler view.
                       // LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false);
                       // RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.idRVBooks);

                        // in below line we are setting layout manager and
                        // adapter to our recycler view.
                       // mRecyclerView.setLayoutManager(linearLayoutManager);
                        //mRecyclerView.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // displaying a toast message when we get any error from API
                    Toast.makeText(CameraActivity.this, "No Data Found" + e, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // also displaying error message in toast.
                Toast.makeText(CameraActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();
            }
        });
        // at last we are adding our json object
        // request in our request queue.
        queue.add(booksObjrequest);
    }






    private class ImageData {
        private int mWidth, mHeight, mStride;
        byte[] mBytes;

        ImageData(byte[] bytes, int nWidth, int nHeight, int nStride) {
            mBytes = bytes;
            mWidth = nWidth;
            mHeight = nHeight;
            mStride = nStride;
        }
    }
}

