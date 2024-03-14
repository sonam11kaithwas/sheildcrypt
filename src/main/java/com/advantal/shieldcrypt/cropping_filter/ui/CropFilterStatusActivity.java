package com.advantal.shieldcrypt.cropping_filter.ui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.cropping_filter.adapter.FiltersAdapter;
import com.advantal.shieldcrypt.cropping_filter.cropping.CropImageView;
import com.advantal.shieldcrypt.cropping_filter.model.FilterData;
import com.advantal.shieldcrypt.tabs_pkg.chat_window_pkg.profile_pkg.ProfileActivity;
import com.advantal.shieldcrypt.tabs_pkg.tab_fragment.StatusFragment;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

/**
 * Created by Prashant Lal on 14-11-2022 12:14.
 */
public class CropFilterStatusActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener,
        CropImageView.OnCropImageCompleteListener {
    private CropImageView mCropImageView;
    // endregion
    private TextView txtCrop, txtFilter, txtSaveFromCropBtn, txtSaveFromFilterBtn;
    private PhotoEditorView photoEditorView;
    private RelativeLayout rlPhotoEditorView, rlCropMainView;
    private RecyclerView recyclerViewFilters;
    private FiltersAdapter filtersAdapter;
    private ArrayList<FilterData> filtersList = new ArrayList<>();
    private String saveClickedEvent = "1";
    private AppCompatImageView imgBackBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_rect);

        initView();
    }

    public void initView(){
        mCropImageView = findViewById(R.id.cropImageView);
        txtCrop = findViewById(R.id.txtCrop);
        txtFilter = findViewById(R.id.txtFilter);
        photoEditorView = findViewById(R.id.photoEditorView);
        rlPhotoEditorView = findViewById(R.id.rlPhotoEditorView);
        recyclerViewFilters = findViewById(R.id.recyclerViewFilters);
        imgBackBtn = findViewById(R.id.imgBackBtn);
        txtSaveFromCropBtn = findViewById(R.id.txtSaveFromCropBtn);
        txtSaveFromFilterBtn = findViewById(R.id.txtSaveFromFilterBtn);
        rlCropMainView = findViewById(R.id.rlCropMainView);

        rlCropMainView.setVisibility(View.VISIBLE);
        rlPhotoEditorView.setVisibility(View.GONE);

        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);

        updateCurrentCropViewOptions();

        //  mCropImageView.setImageResource(R.drawable.ic_man);

        listenerInit();
        getIntentData();
    }

    public void getIntentData(){
        try {
            if (getIntent().getStringExtra("imageUriPath")!=null && !getIntent().getStringExtra("imageUriPath").isEmpty()){
                Uri myUri = Uri.parse(getIntent().getExtras().getString("imageUriPath"));
                if (myUri!=null && !myUri.equals("")){
                    mCropImageView.setImageUriAsync(myUri);
                }
            }
        } catch (ClassCastException e){
            e.printStackTrace();
        }
    }

    public void setAdapter(Bitmap bitmap){
        setFiltersData();
        filtersAdapter = new FiltersAdapter(this, filtersList, bitmap,
                new FiltersAdapter.Callback() {
                    @Override
                    public void onFilterClicked(Bitmap bitmap, PhotoFilter photoFilter) {
                        setFilteredType(bitmap, photoFilter);
                    }
                });
        recyclerViewFilters.setHasFixedSize(true);
        recyclerViewFilters.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFilters.setAdapter(filtersAdapter);
    }

    public void setFiltersData(){
        filtersList.clear();
        filtersList.add(new FilterData("Original", PhotoFilter.NONE));
        filtersList.add(new FilterData("Brightness", PhotoFilter.BRIGHTNESS));
        filtersList.add(new FilterData("Contrast", PhotoFilter.CONTRAST));
        //  filtersList.add(new FilterData("Fill Light", PhotoFilter.FILL_LIGHT));
        //  filtersList.add(new FilterData("Fish Eye", PhotoFilter.FISH_EYE));
        filtersList.add(new FilterData("Grain", PhotoFilter.GRAIN));
        filtersList.add(new FilterData("Gray Scale", PhotoFilter.GRAY_SCALE));
        //   filtersList.add(new FilterData("Lomish", PhotoFilter.LOMISH));
        // filtersList.add(new FilterData("Negative", PhotoFilter.NEGATIVE));
        //filtersList.add(new FilterData("Posterize", PhotoFilter.POSTERIZE));
        filtersList.add(new FilterData("Sepia", PhotoFilter.SEPIA));
        filtersList.add(new FilterData("Saturate", PhotoFilter.SATURATE));
        // filtersList.add(new FilterData("Posterize", PhotoFilter.POSTERIZE));
        filtersList.add(new FilterData("Sharpen", PhotoFilter.SHARPEN));
        // filtersList.add(new FilterData("Temprature", PhotoFilter.TEMPERATURE));
        //  filtersList.add(new FilterData("Tint", PhotoFilter.TINT));
        //  filtersList.add(new FilterData("Vignette", PhotoFilter.VIGNETTE));
    }

    public void listenerInit(){
        txtCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rlCropMainView.getVisibility() != View.VISIBLE){
                    saveClickedEvent = "1";
                    txtSaveFromFilterBtn.setVisibility(View.GONE);
                    txtSaveFromCropBtn.setVisibility(View.VISIBLE);
                    rlCropMainView.setVisibility(View.VISIBLE);
                    rlPhotoEditorView.setVisibility(View.GONE);
                    photoEditorView.saveFilter(new OnSaveBitmap() {
                        @Override
                        public void onBitmapReady(@Nullable Bitmap bitmap) {
                            if (bitmap!=null)
                                mCropImageView.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onFailure(@Nullable Exception e) {

                        }
                    });
                }
            }
        });

        txtFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rlPhotoEditorView.getVisibility() != View.VISIBLE){
                    saveClickedEvent = "2";
                    txtSaveFromFilterBtn.setVisibility(View.VISIBLE);
                    txtSaveFromCropBtn.setVisibility(View.GONE);
                    rlCropMainView.setVisibility(View.GONE);
                    rlPhotoEditorView.setVisibility(View.VISIBLE);
                    mCropImageView.getCroppedImageAsync();
                }
            }
        });

        imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txtSaveFromCropBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (saveClickedEvent)
                Log.e("saveClickedEvent", " ->> " +saveClickedEvent);
                mCropImageView.getCroppedImageAsync();
            }
        });

        txtSaveFromFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoEditorView.saveFilter(new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(@Nullable Bitmap bitmap) {
                        if (bitmap!=null){
                            loadData(bitmap);
                        }
                    }

                    @Override
                    public void onFailure(@Nullable Exception e) {

                    }
                });
            }
        });
    }

    public void loadData(Bitmap bitmap){
        if (bitmap!=null && !bitmap.equals("")){
            OutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(CropFilterStatusActivity.this.getContentResolver(),
                    bitmap, "SK_"+System.currentTimeMillis(), null);
            if (path!=null && !path.isEmpty() && Uri.parse(path) != null) {
                ProfileActivity.MyObject.croppedBitmap = Uri.parse(path).toString();
            }
        } else {
            Toast.makeText(CropFilterStatusActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
        onBackPressed();
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if (result.getError() == null) {
            if (saveClickedEvent.equals("1")){
                loadData(result.getBitmap());
            } else {
                if (result.getBitmap()!=null){
                    setAdapter(result.getBitmap());
                    setFilteredType(result.getBitmap(), PhotoFilter.NONE);
                }
            }
        } else {
            Log.e("AIC", "Failed to crop image", result.getError());
            Toast.makeText(
                            this,
                            "Image crop failed: " + result.getError().getMessage(),
                            Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void setFilteredType(Bitmap bitmap, PhotoFilter photoFilter){
        photoEditorView.getSource().setImageBitmap(bitmap);
        photoEditorView.setFilterEffect(photoFilter);
    }

    public void updateCurrentCropViewOptions() {
        CropImageViewOptions options = new CropImageViewOptions();
        options.scaleType = mCropImageView.getScaleType();
        options.cropShape = mCropImageView.getCropShape();
        options.guidelines = mCropImageView.getGuidelines();
        options.aspectRatio = mCropImageView.getAspectRatio();
        options.fixAspectRatio = mCropImageView.isFixAspectRatio();
        options.showCropOverlay = mCropImageView.isShowCropOverlay();
        options.showProgressBar = mCropImageView.isShowProgressBar();
        options.autoZoomEnabled = mCropImageView.isAutoZoomEnabled();
        options.maxZoomLevel = mCropImageView.getMaxZoom();
        options.flipHorizontally = mCropImageView.isFlippedHorizontally();
        options.flipVertically = mCropImageView.isFlippedVertically();
        //((NewActivity) this).setCurrentOptions(options);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            // Toast.makeText(this, "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("AIC", "Failed to load image by URI", error);
            Toast.makeText(this, "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
        Log.e("Cropping", "  onCropImageComplete " +result.getBitmap());
        handleCropResult(result);
    }
}

