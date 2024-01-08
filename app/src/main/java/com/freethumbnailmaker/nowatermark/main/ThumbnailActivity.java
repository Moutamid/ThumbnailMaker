package com.freethumbnailmaker.nowatermark.main;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

import android.graphics.RectF;
import android.graphics.Typeface;

import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.transition.Explode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.view.PointerIconCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;


import com.google.android.gms.common.Scopes;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.freethumbnailmaker.nowatermark.R;
import com.freethumbnailmaker.nowatermark.activity.BaseActivity;
import com.freethumbnailmaker.nowatermark.adapter.FontAdapter;
import com.freethumbnailmaker.nowatermark.adapter.RecyclerItemClickListener;
import com.freethumbnailmaker.nowatermark.adapter.RecyclerOverLayAdapter;
import com.freethumbnailmaker.nowatermark.adapter.RecyclerTextBgAdapter;
import com.freethumbnailmaker.nowatermark.create.BlurOperationAsync;
import com.freethumbnailmaker.nowatermark.create.DatabaseHandler;
import com.freethumbnailmaker.nowatermark.create.RepeatListener;
import com.freethumbnailmaker.nowatermark.create.TemplateInfo;
import com.freethumbnailmaker.nowatermark.eraser.StickerRemoveActivity;
import com.freethumbnailmaker.nowatermark.fragment.BackgroundFragment;
import com.freethumbnailmaker.nowatermark.fragment.BackgroundFragment2;
import com.freethumbnailmaker.nowatermark.fragment.StickerFragment;
import com.freethumbnailmaker.nowatermark.fragment.StickerFragmentMore;
import com.freethumbnailmaker.nowatermark.interfaces.GetSnapListener;
import com.freethumbnailmaker.nowatermark.interfaces.GetSnapListenerData;
import com.freethumbnailmaker.nowatermark.listener.OnClickCallback;
import com.freethumbnailmaker.nowatermark.model.BackgroundImage;
import com.freethumbnailmaker.nowatermark.model.Sticker_info;
import com.freethumbnailmaker.nowatermark.model.Text_info;
import com.freethumbnailmaker.nowatermark.model.ThumbnailCo;
import com.freethumbnailmaker.nowatermark.text.AutofitTextRel;
import com.freethumbnailmaker.nowatermark.text.TextInfo;
import com.freethumbnailmaker.nowatermark.utility.FilterAdjuster;
import com.freethumbnailmaker.nowatermark.utility.ImageUtils;
import com.freethumbnailmaker.nowatermark.utils.AppPreference;
import com.freethumbnailmaker.nowatermark.utils.ElementInfo;
import com.freethumbnailmaker.nowatermark.view.AutoFitEditText;
import com.freethumbnailmaker.nowatermark.view.StickerView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cz.msebera.android.httpclient.cookie.ClientCookie;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageGaussianBlurFilter;

import com.freethumbnailmaker.nowatermark.colorpicker.LineColorPicker;
import com.freethumbnailmaker.nowatermark.colorpicker.OnColorChangedListener;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ThumbnailActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, GetColorListener, GetSnapListenerData, OnSetImageSticker, GetSnapListener, StickerView.TouchEventListener, AutofitTextRel.TouchEventListener, RecyclerOverLayAdapter.OnOverlaySelected {

    private static final int SELECT_PICTURE_FROM_CAMERA = 905;
    private static final int SELECT_PICTURE_FROM_GALLERY = 907;
    private static final int SELECT_PICTURE_FROM_GALLERY_BACKGROUND = 909;
    private static final String TAG = "ThumbnailActivity";
    private static final int TEXT_ACTIVITY = 908;

    private static final int TYPE_STICKER = 9072;
    public static ThumbnailActivity activity = null;
    public static ImageView background_img = null;
    public static Bitmap btmSticker = null;
    public static ImageView btn_layControls = null;
    public static Activity context = null;
    public static Bitmap imgBtmap = null;
    public static boolean isUpadted = false;
    public static boolean isUpdated = false;
    public static FrameLayout lay_container;
    public static int mRadius;
    public static SeekBar seek_tailys;
    public static RelativeLayout txtStkrRel;
    public static Bitmap withoutWatermark;
    boolean OneShow = true;
    FontAdapter adapter;
    RecyclerOverLayAdapter adaptor_overlay;
    RecyclerTextBgAdapter adaptor_txtBg;
    int alpha = 80;
    private SeekBar alphaSeekbar;
    private Animation animSlideDown;

    public Animation animSlideUp;
    private AppPreference appPreference;

    public Bitmap bit;
    private final int bColor = Color.parseColor("#4149b6");
    int backgroundOrientation = 2;
    ImageView background_blur;
    int bgAlpha = 0;
    int bgColor = ViewCompat.MEASURED_STATE_MASK;
    String bgDrawable = "0";
    LinearLayout bgShow;

    public Bitmap bitmap;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.e(ThumbnailActivity.TAG, "onReceive: ");
            ThumbnailActivity.this.lay_background.setVisibility(View.GONE);
            ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
            thumbnailActivity.backgroundOrientation = 2;
            thumbnailActivity.openCustomActivity(null, intent);
        }
    };

    ImageView btnColorBackgroundPic;
    ImageView btnEditControlBg;
    ImageView btnEditControlColor;
    ImageView btnEditControlOutlineColor;
    ImageView btnEditControlShadowColor;
    ImageView btnImgBackground;
    ImageView btnImgCameraSticker;

    ImageView btnRedo;
    ImageView btnShadowBottom;
    ImageView btnShadowLeft;
    ImageView btnShadowRight;
    ImageView btnShadowTop;
    ImageView btnTakePicture;
    ImageView btnUndo;


    public ImageButton btn_bck1;
    ImageView btn_erase;
    ImageButton btn_up_down;
    ImageButton btn_up_down1;
    int cat_id;

    public RelativeLayout center_rel;
    boolean checkMemory;

    public boolean checkTouchContinue = false;
    LinearLayout colorShow;
    String color_Type;
    LinearLayout controlsShow;
    LinearLayout controlsShowStkr;
    private final int count_stkr = 7;
    private final int curTileId = 0;
    ProgressDialog dialogIs;
    boolean dialogShow = true;
    float distance;
    int distanceScroll;

    int dsfc;

    public boolean editMode = false;
    ArrayList<ElementInfo> elementInfosU_R = new ArrayList<>();


    private File file;

    public String filename;
    private View focusedCopy = null;

    public View focusedView;
    String fontName = "";
    LinearLayout fontsCurve;
    LinearLayout fontsShow;
    LinearLayout fontsSpacing;
    String frame_Name = "";
    ImageView guideline;
    String hex;
    private LineColorPicker horizontalPicker;
    private LineColorPicker horizontalPickerColor;


    public float hr = 1.0f;
    private LinearLayout hsv;
    private SeekBar hueSeekbar;

    ImageView img_oK;

    private boolean isBackground;


    RelativeLayout lay_StkrMain;
    RelativeLayout lay_TextMain;
    LinearLayout lay_background;
    RelativeLayout lay_color;
    LinearLayout lay_colorOacity;
    RelativeLayout lay_colorOpacity;
    RelativeLayout lay_controlStkr;
    LinearLayout lay_dupliStkr;
    ImageView lay_dupliText;
    ImageView lay_edit;
    private LinearLayout lay_effects;
    RelativeLayout lay_filter;

    public LinearLayout lay_fonts_Spacing;
    RelativeLayout lay_handletails;
    RelativeLayout lay_hue;
    private RelativeLayout lay_remove;
    ScrollView lay_scroll;
    LinearLayout lay_sticker;
    private LinearLayout lay_textEdit;
    private LinearLayout layoutEffectView;
    private LinearLayout layoutFilterView;

    public RelativeLayout layoutShadow1;

    public RelativeLayout layoutShadow2;
    int leftRightShadow = 0;
    private float letterSpacing = 0.0f;
    private float lineSpacing = 0.0f;
    ListFragment listFragment;
    private final List<WeakReference<Fragment>> mFragments = new ArrayList();

    public Handler mHandler;


    public int mInterval = 50;

    public Runnable mStatusChecker;
    FrameLayout mViewAllFrame;

    public RelativeLayout main_rel;
    private int min = 0;

    int myDesignFlag;
    BitmapFactory.Options options = new BitmapFactory.Options();

    public int outerColor = 0;

    public int outerSize = 0;
    LinearLayout outlineShow;
    String overlay_Name = "";
    int overlay_blur;
    int overlay_opacty;
    String[] pallete = {"#ffffff", "#cccccc", "#999999", "#666666", "#333333", "#000000", "#ffee90", "#ffd700", "#daa520", "#b8860b", "#ccff66", "#adff2f", "#00fa9a", "#00ff7f", "#00ff00", "#32cd32", "#3cb371", "#99cccc", "#66cccc", "#339999", "#669999", "#006666", "#336666", "#ffcccc", "#ff9999", "#ff6666", "#ff3333", "#ff0033", "#cc0033"};
    float parentY;
    private LineColorPicker pickerBg;
    private LineColorPicker pickerOutline;
    String position = "0";
    int post_id;
    private int processs;
    String profile;
    ProgressBar progressBarUndo;
    String ratio;
    RelativeLayout rellative;
    float rotation = 0.0f;
    LinearLayout sadowShow;
    float screenHeight;
    float screenWidth;

    public SeekBar seek;

    public SeekBar seekBar3;

    public SeekBar seekBar_shadow;
    private SeekBar seekLetterSpacing;
    private SeekBar seekLineSpacing;
    private SeekBar seekOutlineSize;
    private SeekBar seekShadowBlur;

    public int seekValue = 90;

    public SeekBar seek_blur;
    private LinearLayout seekbar_container;
    private LinearLayout seekbar_handle;
    int shadowColor = ViewCompat.MEASURED_STATE_MASK;

    public int shadowFlag = 0;
    private LineColorPicker shadowPickerColor;
    int shadowProg = 0;

    RelativeLayout shape_rel;
    boolean showtailsSeek = false;
    int sizeFull = 0;
    ArrayList<Sticker_info> stickerInfoArrayList = new ArrayList<>();
    int stkrColorSet = Color.parseColor("#ffffff");
    int tAlpha = 100;
    int tColor = -1;
    int tempID = 2001;
    String temp_Type = "";
    String temp_path = "";

    public List<TemplateInfo> templateList = new ArrayList();
    ArrayList<TemplateInfo> templateListR_U = new ArrayList<>();
    ArrayList<TemplateInfo> templateListU_R = new ArrayList<>();
    int template_id;
    int textColorSet = Color.parseColor("#ffffff");
    ArrayList<Text_info> textInfoArrayList = new ArrayList<>();
    ArrayList<TextInfo> textInfosU_R = new ArrayList<>();
    int topBottomShadow = 0;
    ImageView trans_img;
    private Typeface ttf;
    private Typeface ttfHeader;
    TextView txtBG;
    private TextView txtBgControl;
    private TextView txtColorOpacity;
    private TextView txtColorsControl;
    private TextView txtControlText;
    TextView txtEffect;
    private TextView txtEffectText;
    private TextView txtFilterText;
    private TextView txtFontsControl;
    TextView txtImage;
    private TextView txtShadowControl;
    HashMap<Integer, Object> txtShapeList;
    TextView txtSticker;
    TextView txtText;
    private TextView txtTextControls;
    private TextView txt_fonts_Spacing;
    private TextView txt_fonts_Style;
    private TextView txt_fonts_curve;
    private TextView txt_outline_control;
    private RelativeLayout user_image;
    SeekBar verticalSeekBar = null;


    public float wr = 1.0f;
    float yAtLayoutCenter = -1.0f;

    private float getnewHeight(int i, int i2, float f, float f2) {
        return (((float) i2) * f) / ((float) i);
    }

    private float getnewWidth(int i, int i2, float f, float f2) {
        return (((float) i) * f2) / ((float) i2);
    }

    public void onEdit(View view, Uri uri) {
    }

    public void onMidX(View view) {
    }

    public void onMidXY(View view) {
    }

    public void onMidY(View view) {
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onXY(View view) {
    }


    public float getXpos(float f) {
        return (((float) this.main_rel.getWidth()) * f) / 100.0f;
    }


    public float getYpos(float f) {
        return (((float) this.main_rel.getHeight()) * f) / 100.0f;
    }


    public int getNewWidht(float f, float f2) {
        return (int) ((((float) this.main_rel.getWidth()) * (f2 - f)) / 100.0f);
    }


    public int getNewHeight(float f, float f2) {
        return (int) ((((float) this.main_rel.getHeight()) * (f2 - f)) / 100.0f);
    }


    public int getNewHeightText(float f, float f2) {
        float height = (((float) this.main_rel.getHeight()) * (f2 - f)) / 100.0f;
        return (int) (((float) ((int) height)) + (height / 2.0f));
    }


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_poster);
        this.appPreference = new AppPreference(getApplicationContext());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.screenWidth = (float) displayMetrics.widthPixels;
        this.screenHeight = (float) (displayMetrics.heightPixels - ImageUtils.dpToPx(this, 105.0f));

        AndroidNetworking.initialize(getApplicationContext());
        if (Build.VERSION.SDK_INT < 8 && Build.VERSION.SDK_INT >= 21) {
            Explode explode = new Explode();
            explode.setDuration(400);
            getWindow().setEnterTransition(explode);
            getWindow().setExitTransition(explode);
        }
        findView();
        intilization();


        context = this;
        activity = this;
        this.options.inScaled = false;
        this.ttfHeader = Constants.getHeaderTypeface(this);
        ((TextView) findViewById(R.id.txtheader)).setTypeface(setBoldFont());
        this.myDesignFlag = getIntent().getIntExtra("cat_id", 0);
        this.cat_id = getIntent().getIntExtra("cat_id", 0);
        this.post_id = getIntent().getIntExtra("pos_id", 0);
        if (this.myDesignFlag != 0) {
            ArrayList parcelableArrayListExtra = getIntent().getParcelableArrayListExtra("template");
            this.textInfoArrayList = getIntent().getParcelableArrayListExtra("text");
            this.stickerInfoArrayList = getIntent().getParcelableArrayListExtra("sticker");
            this.profile = getIntent().getStringExtra(Scopes.PROFILE);
            this.temp_path = ((ThumbnailCo) parcelableArrayListExtra.get(0)).getBack_image();
            ThumbnailCo thumbnailCo = (ThumbnailCo) parcelableArrayListExtra.get(0);
            this.post_id = Integer.parseInt(thumbnailCo.getPost_id());
            this.template_id = Integer.parseInt(thumbnailCo.getPost_id());
            this.frame_Name = thumbnailCo.getBack_image();
            this.ratio = getIntent().getStringExtra("sizeposition");
            this.dialogIs = new ProgressDialog(this);
            this.dialogIs.setMessage(getResources().getString(R.string.plzwait));
            this.dialogIs.setCancelable(false);
            this.dialogIs.show();
            drawBackgroundImageFromDp(this.ratio, this.position, this.profile, "created");
        } else if (getIntent().getBooleanExtra("loadUserFrame", false)) {
            Bundle extras = getIntent().getExtras();
            this.ratio = extras.getString("ratio");
            if (!extras.getString("ratio").equals("cropImg")) {
                this.ratio = extras.getString("ratio");
                this.position = extras.getString("position");
                this.profile = extras.getString(Scopes.PROFILE);
                this.hex = extras.getString("hex");
                drawBackgroundImage(this.ratio, this.position, this.profile, "nonCreated");
            } else if (extras.getString("ratio").equals("cropImg")) {
                this.ratio = "";
                this.position = "1";
                this.profile = "Temp_Path";
                this.hex = "";
                setImageBitmapAndResizeLayout(ImageUtils.resizeBitmap(Constants.bitmap, (int) this.screenWidth, (int) this.screenHeight), "nonCreated");
            }
        } else {
            this.temp_Type = getIntent().getExtras().getString("Temp_Type");
            DatabaseHandler dbHandler = DatabaseHandler.getDbHandler(getApplicationContext());
            if (this.temp_Type.equals("MY_TEMP")) {
                this.templateList = dbHandler.getTemplateListDes("USER");
            } else if (this.temp_Type.equals("FREE_TEMP")) {
                this.templateList = dbHandler.getTemplateList("FREESTYLE");
            } else if (this.temp_Type.equals("FRIDAY_TEMP")) {
                this.templateList = dbHandler.getTemplateList("FRIDAY");
            } else if (this.temp_Type.equals("SALE_TEMP")) {
                this.templateList = dbHandler.getTemplateList("SALES");
            } else if (this.temp_Type.equals("SPORT_TEMP")) {
                this.templateList = dbHandler.getTemplateList("SPORTS");
            }
            dbHandler.close();
            final int intExtra = getIntent().getIntExtra("position", 0);
            this.center_rel.post(new Runnable() {
                public void run() {
                    LordTemplateAsync lordTemplateAsync = new LordTemplateAsync();
                    lordTemplateAsync.execute("" + intExtra);
                }
            });
        }
        int[] iArr = new int[this.pallete.length];
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = Color.parseColor(this.pallete[i]);
        }
        this.horizontalPicker.setColors(iArr);
        this.horizontalPickerColor.setColors(iArr);
        this.shadowPickerColor.setColors(iArr);
        this.pickerOutline.setColors(iArr);
        this.pickerBg.setColors(iArr);
        this.horizontalPicker.setSelectedColor(this.textColorSet);
        this.horizontalPickerColor.setSelectedColor(this.stkrColorSet);
        this.shadowPickerColor.setSelectedColor(iArr[5]);
        this.pickerOutline.setSelectedColor(iArr[5]);
        this.pickerBg.setSelectedColor(iArr[5]);
        int color = this.horizontalPicker.getColor();
        int color2 = this.horizontalPickerColor.getColor();
        int color3 = this.shadowPickerColor.getColor();
        int color4 = this.pickerBg.getColor();
        int color5 = this.pickerOutline.getColor();
        updateColor(color);
        updateColor(color2);
        updateShadow(color3);
        updateOutline(color5);
        updateBgColor(color4);
        this.horizontalPickerColor.setOnColorChangedListener(new OnColorChangedListener() {
            public void onColorChanged(int i) {
                ThumbnailActivity.this.updateColor(i);
            }
        });
        this.horizontalPicker.setOnColorChangedListener(new OnColorChangedListener() {
            public void onColorChanged(int i) {
                ThumbnailActivity.this.updateColor(i);
            }
        });
        this.shadowPickerColor.setOnColorChangedListener(new OnColorChangedListener() {
            public void onColorChanged(int i) {
                ThumbnailActivity.this.updateShadow(i);
            }
        });
        this.pickerOutline.setOnColorChangedListener(new OnColorChangedListener() {
            public void onColorChanged(int i) {
                ThumbnailActivity.this.updateOutline(i);
            }
        });
        this.pickerBg.setOnColorChangedListener(new OnColorChangedListener() {
            public void onColorChanged(int i) {
                ThumbnailActivity.this.updateBgColor(i);
            }
        });
        this.mViewAllFrame = findViewById(R.id.viewall_layout);
        this.guideline = findViewById(R.id.guidelines);
        this.rellative = findViewById(R.id.rellative);
        this.lay_scroll = findViewById(R.id.lay_scroll);
        this.lay_scroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ThumbnailActivity.this.onTouchApply();
                return true;
            }
        });
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams.addRule(13);
        this.lay_scroll.setLayoutParams(layoutParams);
        this.lay_scroll.postInvalidate();
        this.lay_scroll.requestLayout();
        findViewById(R.id.btnLeft).setOnTouchListener(new RepeatListener(200, 100, this.guideline, new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.updatePositionSticker("decX");
            }
        }));
        findViewById(R.id.btnUp).setOnTouchListener(new RepeatListener(200, 100, this.guideline, new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.updatePositionSticker("incrX");
            }
        }));
        findViewById(R.id.btnRight).setOnTouchListener(new RepeatListener(200, 100, this.guideline, new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.updatePositionSticker("decY");
            }
        }));
        findViewById(R.id.btnDown).setOnTouchListener(new RepeatListener(200, 100, this.guideline, new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.updatePositionSticker("incrY");
            }
        }));
        findViewById(R.id.btnLeftS).setOnTouchListener(new RepeatListener(200, 100, this.guideline, new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.updatePositionSticker("decX");
            }
        }));
        findViewById(R.id.btnRightS).setOnTouchListener(new RepeatListener(200, 100, this.guideline, new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.updatePositionSticker("incrX");
            }
        }));
        findViewById(R.id.btnUpS).setOnTouchListener(new RepeatListener(200, 100, this.guideline, new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.updatePositionSticker("decY");
            }
        }));
        findViewById(R.id.btnDownS).setOnTouchListener(new RepeatListener(200, 100, this.guideline, new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.updatePositionSticker("incrY");
            }
        }));
        this.mHandler = new Handler();
        this.mStatusChecker = new Runnable() {
            public void run() {
                if (ThumbnailActivity.this.lay_scroll != null) {
                    ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
                    thumbnailActivity.removeScrollViewPosition(thumbnailActivity.focusedView);
                }
                ThumbnailActivity.this.mHandler.postDelayed(this, ThumbnailActivity.this.mInterval);
            }
        };
        oneTimeLayerAdjust();
    }

    public void removeScrollViewPosition(View view) {
        float f;
        int[] iArr = new int[2];
        this.lay_scroll.getLocationOnScreen(iArr);
        float f2 = (float) iArr[1];
        float width = (float) view.getWidth();
        float height = (float) view.getHeight();
        float x = view.getX();
        float y = (view.getY() + f2) - ((float) this.distanceScroll);
        if (view instanceof StickerView) {
            f = view.getRotation();
        } else {
            f = view.getRotation();
        }
        Matrix matrix = new Matrix();
        RectF rectF = new RectF(x, y, x + width, y + height);
        matrix.postRotate(f, x + (width / 2.0f), (height / 2.0f) + y);
        matrix.mapRect(rectF);
        float min2 = Math.min(rectF.top, rectF.bottom);
        if (f2 > min2) {
            float f3 = (float) ((int) (f2 - min2));
            try {
                float scrollY = (float) this.lay_scroll.getScrollY();
                if (scrollY > 0.0f) {
                    float f4 = scrollY - ((float) (((int) f3) / 4));
                    if (f4 >= 0.0f) {
                        this.lay_scroll.smoothScrollTo(0, (int) f4);
                        this.lay_scroll.getLayoutParams().height = (int) (((float) this.lay_scroll.getHeight()) + (y / 4.0f));
                        this.lay_scroll.postInvalidate();
                        this.lay_scroll.requestLayout();
                        return;
                    }
                    this.distanceScroll = 0;
                    this.lay_scroll.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
                    this.lay_scroll.postInvalidate();
                    this.lay_scroll.requestLayout();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private void findView() {
        this.progressBarUndo = findViewById(R.id.progress_undo);
        this.btnUndo = findViewById(R.id.btn_undo);
        this.btnRedo = findViewById(R.id.btn_redo);
        this.btnUndo.setOnClickListener(this);
        this.btnRedo.setOnClickListener(this);
        this.btn_bck1 = findViewById(R.id.btn_bck1);
        this.btn_bck1.setOnClickListener(this);
        this.hsv = findViewById(R.id.layHint);
        this.txtTextControls = findViewById(R.id.txt_text_controls);
        this.txtFontsControl = findViewById(R.id.txt_fonts_control);
        this.txt_fonts_Style = findViewById(R.id.txt_fonts_Style);
        this.lay_fonts_Spacing = findViewById(R.id.lay_fonts_Spacing);
        this.txt_fonts_Spacing = findViewById(R.id.txt_fonts_Spacing);
        this.txt_fonts_curve = findViewById(R.id.txt_fonts_curve);
        this.txtColorsControl = findViewById(R.id.txt_colors_control);
        this.txtShadowControl = findViewById(R.id.txt_shadow_control);
        this.txt_outline_control = findViewById(R.id.txt_outline_control);
        this.txtBgControl = findViewById(R.id.txt_bg_control);


        this.btnEditControlColor = findViewById(R.id.btnEditControlColor);
        this.btnEditControlShadowColor = findViewById(R.id.btnEditControlShadowColor);
        this.btnEditControlOutlineColor = findViewById(R.id.btnEditControlOutlineColor);
        this.btnEditControlBg = findViewById(R.id.btnEditControlBg);
        this.btnShadowLeft = findViewById(R.id.btnShadowLeft);
        this.btnShadowRight = findViewById(R.id.btnShadowRight);
        this.btnShadowTop = findViewById(R.id.btnShadowTop);
        this.btnShadowBottom = findViewById(R.id.btnShadowBottom);
        this.btn_erase = findViewById(R.id.btn_erase);
        this.txtEffectText = findViewById(R.id.txtEffectText);
        this.txtFilterText = findViewById(R.id.txtFilterText);
        this.layoutEffectView = findViewById(R.id.layoutEffectView);
        this.layoutFilterView = findViewById(R.id.layoutFilterView);
        ImageView btnShadowTabChange = findViewById(R.id.btnShadowTabChange);
        this.layoutShadow1 = findViewById(R.id.layoutShadow1);
        this.layoutShadow2 = findViewById(R.id.layoutShadow2);
        this.txtText = findViewById(R.id.bt_text);
        this.txtSticker = findViewById(R.id.bt_sticker);
        this.txtImage = findViewById(R.id.bt_image);
        this.txtEffect = findViewById(R.id.bt_effect);
        this.txtBG = findViewById(R.id.bt_bg);
        this.btn_erase.setOnClickListener(this);
        btnShadowTabChange.setOnClickListener(view -> {
            if (ThumbnailActivity.this.shadowFlag == 0) {
                ThumbnailActivity.this.shadowFlag = 1;
                ThumbnailActivity.this.layoutShadow2.setVisibility(View.VISIBLE);
                ThumbnailActivity.this.layoutShadow1.setVisibility(View.GONE);
            } else if (ThumbnailActivity.this.shadowFlag == 1) {
                ThumbnailActivity.this.shadowFlag = 0;
                ThumbnailActivity.this.layoutShadow1.setVisibility(View.VISIBLE);
                ThumbnailActivity.this.layoutShadow2.setVisibility(View.GONE);
            }
        });
    }

    private void drawBackgroundImageFromDp(final String str, String str2, String str3, final String str4) {
        this.lay_sticker.setVisibility(View.GONE);
        File file = new File(this.frame_Name);
        Log.e("file", "==" + file);
        if (file.exists()) {
            RequestBuilder<Bitmap> asBitmap = Glide.with(getApplicationContext()).asBitmap();
            RequestOptions requestOptions = new RequestOptions().skipMemoryCache(true);
            float f = this.screenWidth;
            float f2 = this.screenHeight;
            if (f <= f2) {
                f = f2;
            }
            asBitmap.apply(requestOptions.override((int) f)).load(this.frame_Name).into(new SimpleTarget<Bitmap>() {
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    ThumbnailActivity.this.bitmapRatio(str, "Background", bitmap, str4);
                }
            });
        } else if (!str.equals("")) {
            String replace = file.getName().replace(".png", "");
            new SavebackgrundAsync().execute(replace, str, str3, str4);
        } else if (this.OneShow) {
            errorDialogTempInfo();
            this.OneShow = false;
        }
    }


    public void drawBackgroundImage(final String str, String str2, String str3, final String str4) {
        this.lay_sticker.setVisibility(View.GONE);
        if (new File(this.profile).exists()) {
            try {
                bitmapRatio(str, str3, ImageUtils.getResampleImageBitmap(Uri.parse(this.profile), this, (int) (this.screenWidth > this.screenHeight ? this.screenWidth : this.screenHeight)), str4);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Glide.with(getApplicationContext()).asBitmap().apply(new RequestOptions().skipMemoryCache(true)).load(str3).into(new SimpleTarget<Bitmap>() {
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        ThumbnailActivity.imgBtmap = bitmap;
                        ThumbnailActivity.this.bitmapRatio(str, "Background", bitmap, str4);
                    }
                });
            } catch (NullPointerException e2) {
                e2.printStackTrace();
                Glide.with(getApplicationContext()).asBitmap().apply(new RequestOptions().skipMemoryCache(true)).load(str3).into(new SimpleTarget<Bitmap>() {
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        ThumbnailActivity.this.bitmapRatio(str, "Background", bitmap, str4);
                    }
                });
            }
        }
    }


    public void bitmapRatio(String str, String str2, Bitmap bitmap2, String str3) {
        String[] split = str.split(":");
        int gcd = gcd(Integer.parseInt(split[0]), Integer.parseInt(split[1]));

        Integer.parseInt(split[0]);
        Integer.parseInt(split[1]);
        String str4 = "" + (Integer.parseInt(split[0]) / gcd) + ":" + (Integer.parseInt(split[1]) / gcd);
        if (!str4.equals("")) {
            if (str4.equals("1:1")) {
                bitmap2 = cropInRatio(bitmap2, 1, 1);
            } else if (str4.equals("16:9")) {
                bitmap2 = cropInRatio(bitmap2, 16, 9);
            } else if (str4.equals("9:16")) {
                bitmap2 = cropInRatio(bitmap2, 9, 16);
            } else if (str4.equals("4:3")) {
                bitmap2 = cropInRatio(bitmap2, 4, 3);
            } else if (str4.equals("3:4")) {
                bitmap2 = cropInRatio(bitmap2, 3, 4);
            } else {
                bitmap2 = cropInRatio(bitmap2, Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            }
        }
        Bitmap resizeBitmap = Constants.resizeBitmap(bitmap2, (int) this.screenWidth, (int) this.screenHeight);
        if (!str3.equals("created")) {
            if (str2.equals("Texture")) {
                setImageBitmapAndResizeLayout(Constants.getTiledBitmap(this, this.curTileId, resizeBitmap, seek_tailys), "nonCreated");
            } else {
                setImageBitmapAndResizeLayout(resizeBitmap, "nonCreated");
            }
        } else if (str2.equals("Texture")) {
            setImageBitmapAndResizeLayout(Constants.getTiledBitmap(this, this.curTileId, resizeBitmap, seek_tailys), "created");
        } else {
            setImageBitmapAndResizeLayout(resizeBitmap, "created");
        }
    }


    public Bitmap cropInRatio(Bitmap bitmap2, int i, int i2) {
        float width = (float) bitmap2.getWidth();
        float height = (float) bitmap2.getHeight();
        float f = getnewHeight(i, i2, width, height);
        float f2 = getnewWidth(i, i2, width, height);
        return (f2 == width && f == height) ? bitmap2 : (f > height || f >= height) ? (f2 > width || f2 >= width) ? null : Bitmap.createBitmap(bitmap2, (int) ((width - f2) / 2.0f), 0, (int) f2, (int) height) : Bitmap.createBitmap(bitmap2, 0, (int) ((height - f) / 2.0f), (int) width, (int) f);
    }


    private void setImageBitmapAndResizeLayout(Bitmap bitmap2, String str) {
        this.main_rel.getLayoutParams().width = bitmap2.getWidth();
        this.main_rel.getLayoutParams().height = bitmap2.getHeight();
        this.main_rel.postInvalidate();
        this.main_rel.requestLayout();
        background_img.setImageBitmap(bitmap2);
        imgBtmap = bitmap2;
        this.bit = bitmap2;
        this.main_rel.post(() -> {
            ThumbnailActivity.this.guideline.setImageBitmap(Constants.guidelines_bitmap(ThumbnailActivity.activity, ThumbnailActivity.this.main_rel.getWidth(), ThumbnailActivity.this.main_rel.getHeight()));
            ThumbnailActivity.this.lay_scroll.post(new Runnable() {
                public void run() {
                    int[] iArr = new int[2];
                    ThumbnailActivity.this.lay_scroll.getLocationOnScreen(iArr);
                    ThumbnailActivity.this.parentY = (float) iArr[1];
                    ThumbnailActivity.this.yAtLayoutCenter = ThumbnailActivity.this.parentY;
                }
            });
            try {
                ThumbnailActivity.this.bit = ImageUtils.resizeBitmap(ThumbnailActivity.this.bit, ThumbnailActivity.this.center_rel.getWidth(), ThumbnailActivity.this.center_rel.getHeight());
                float height = (float) ThumbnailActivity.this.bit.getHeight();
                ThumbnailActivity.this.wr = ((float) ThumbnailActivity.this.bit.getWidth()) / ((float) ThumbnailActivity.this.bit.getWidth());
                ThumbnailActivity.this.hr = height / ((float) ThumbnailActivity.this.bit.getHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if (this.min != 0) {
            this.background_blur.setVisibility(View.VISIBLE);
        } else {
            this.background_blur.setVisibility(View.GONE);
        }
        if (str.equals("created")) {
            new BlurOperationTwoAsync(this, this.bit, this.background_blur).execute("");
            return;
        }
        new BlurOperationAsync(this, this.bit, this.background_blur).execute("");
    }

    private void intilization() {
        this.ttf = Constants.getTextTypeface(this);
        lay_container = findViewById(R.id.lay_container);
        this.center_rel = findViewById(R.id.center_rel);

        this.btnImgCameraSticker = findViewById(R.id.btnImgCameraSticker);
        this.btnImgBackground = findViewById(R.id.btnImgBackground);
        this.btnTakePicture = findViewById(R.id.btnTakePicture);
        this.btnColorBackgroundPic = findViewById(R.id.btnColorBackgroundPic);

        this.lay_remove = findViewById(R.id.lay_remove);
        this.lay_TextMain = findViewById(R.id.lay_TextMain);
        this.lay_StkrMain = findViewById(R.id.lay_StkrMain);
        this.btn_up_down = findViewById(R.id.btn_up_down);
        this.btn_up_down1 = findViewById(R.id.btn_up_down1);
        this.main_rel = findViewById(R.id.main_rel);
        background_img = findViewById(R.id.background_img);
        this.background_blur = findViewById(R.id.background_blur);
        txtStkrRel = findViewById(R.id.txt_stkr_rel);
        this.user_image = findViewById(R.id.select_artwork);
        RelativeLayout select_backgnd = findViewById(R.id.select_backgnd);
        RelativeLayout select_effect = findViewById(R.id.select_effect);
        RelativeLayout add_sticker = findViewById(R.id.add_sticker);
        RelativeLayout add_text = findViewById(R.id.add_text);
        this.lay_effects = findViewById(R.id.lay_effects);
        this.lay_sticker = findViewById(R.id.lay_sticker);
        this.lay_background = findViewById(R.id.lay_background);
        this.lay_handletails = findViewById(R.id.lay_handletails);
        this.seekbar_container = findViewById(R.id.seekbar_container);
        this.seekbar_handle = findViewById(R.id.seekbar_handle);
        this.shape_rel = findViewById(R.id.shape_rel);
        seek_tailys = findViewById(R.id.seek_tailys);
        this.alphaSeekbar = findViewById(R.id.alpha_seekBar);
        this.seekBar3 = findViewById(R.id.seekBar3);
        this.seekBar_shadow = findViewById(R.id.seekBar_shadow);
        SeekBar seekTextCurve = findViewById(R.id.seekTextCurve);
        this.hueSeekbar = findViewById(R.id.hue_seekBar);
        this.seekShadowBlur = findViewById(R.id.seekShadowBlur);
        this.seekOutlineSize = findViewById(R.id.seekOutlineSize);
        this.trans_img = findViewById(R.id.trans_img);
        this.alphaSeekbar.setOnSeekBarChangeListener(this);
        this.seekBar3.setOnSeekBarChangeListener(this);
        this.seekBar_shadow.setOnSeekBarChangeListener(this);
        this.hueSeekbar.setOnSeekBarChangeListener(this);
        seek_tailys.setOnSeekBarChangeListener(this);
        this.seek = findViewById(R.id.seek);
        this.lay_filter = findViewById(R.id.lay_filter);
        this.lay_dupliText = findViewById(R.id.lay_dupliText);
        this.lay_dupliStkr = findViewById(R.id.lay_dupliStkr);
        this.lay_edit = findViewById(R.id.lay_edit);
        this.lay_dupliText.setOnClickListener(this);
        this.lay_dupliStkr.setOnClickListener(this);
        this.lay_edit.setOnClickListener(this);
        this.seek_blur = findViewById(R.id.seek_blur);

        this.img_oK = findViewById(R.id.btn_done);
        btn_layControls = findViewById(R.id.btn_layControls);
        this.lay_textEdit = findViewById(R.id.lay_textEdit);
        this.verticalSeekBar = findViewById(R.id.seekBar2);
        this.horizontalPicker = findViewById(R.id.picker);
        this.horizontalPickerColor = findViewById(R.id.picker1);
        this.shadowPickerColor = findViewById(R.id.pickerShadow);
        this.pickerOutline = findViewById(R.id.pickerOutline);
        this.pickerBg = findViewById(R.id.pickerBg);
        this.lay_color = findViewById(R.id.lay_color);
        this.lay_hue = findViewById(R.id.lay_hue);

        this.txtControlText = findViewById(R.id.txtControlText);
        this.txtColorOpacity = findViewById(R.id.txtColorOpacity);
        this.seekLetterSpacing = findViewById(R.id.seekLetterSpacing);
        this.seekLineSpacing = findViewById(R.id.seekLineSpacing);
        this.hueSeekbar.setProgress(1);
        this.seek.setMax(255);
        this.seek.setProgress(80);
        this.seek_blur.setMax(255);
        this.seekBar_shadow.setProgress(5);
        this.seekBar3.setProgress(255);
        this.seek_blur.setProgress(this.min);
        this.trans_img.setImageAlpha(this.alpha);
        seek_tailys.setMax(290);
        seek_tailys.setProgress(90);
        this.seek.setOnSeekBarChangeListener(this);
        this.seek_blur.setOnSeekBarChangeListener(this);
        this.img_oK.setOnClickListener(this);
        btn_layControls.setOnClickListener(this);
        this.user_image.setOnClickListener(this);
        select_backgnd.setOnClickListener(this);
        select_effect.setOnClickListener(this);
        add_sticker.setOnClickListener(this);
        add_text.setOnClickListener(this);
        this.lay_remove.setOnClickListener(this);
        this.center_rel.setOnClickListener(this);
        this.animSlideUp = Constants.getAnimUp(this);
        this.animSlideDown = Constants.getAnimDown(this);
        this.verticalSeekBar.setOnSeekBarChangeListener(this);
        this.btnImgCameraSticker.setOnClickListener(this);
        this.btnImgBackground.setOnClickListener(this);
        this.btnColorBackgroundPic.setOnClickListener(this);
        this.btnTakePicture.setOnClickListener(this);
        initOverlayRecycler();
        StickerCategoryVertical();
        BackgroundCategoryVertical();
        fackClick();
        this.seekLetterSpacing.setOnSeekBarChangeListener(this);
        this.seekLineSpacing.setOnSeekBarChangeListener(this);
        seekTextCurve.setOnSeekBarChangeListener(this);
        this.seekShadowBlur.setOnSeekBarChangeListener(this);
        this.seekOutlineSize.setOnSeekBarChangeListener(this);
        this.fontsShow = findViewById(R.id.fontsShow);
        this.fontsSpacing = findViewById(R.id.fontsSpacing);
        this.fontsCurve = findViewById(R.id.fontsCurve);
        this.colorShow = findViewById(R.id.colorShow);
        this.sadowShow = findViewById(R.id.sadowShow);
        this.outlineShow = findViewById(R.id.outlineShow);
        this.bgShow = findViewById(R.id.bgShow);
        this.controlsShow = findViewById(R.id.controlsShow);
        this.adapter = new FontAdapter(this, getResources().getStringArray(R.array.fonts_array));
        this.adapter.setSelected(0);
        ((GridView) findViewById(R.id.font_gridview)).setAdapter(this.adapter);
        this.adapter.setItemClickCallback((OnClickCallback<ArrayList<String>, Integer, String, Activity>) (arrayList, num, str, activity) -> {
            ThumbnailActivity.this.setTextFonts(str);
            ThumbnailActivity.this.adapter.setSelected(num.intValue());
        });
        this.adaptor_txtBg = new RecyclerTextBgAdapter(this, Constants.imageId);
        RecyclerView recyclerView = findViewById(R.id.txtBg_recylr);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.adaptor_txtBg);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, (view, i) -> {
            ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
            thumbnailActivity.setTextBgTexture("btxt" + i);
        }));
        this.lay_colorOpacity = findViewById(R.id.lay_colorOpacity);
        this.lay_controlStkr = findViewById(R.id.lay_controlStkr);
        this.lay_colorOacity = findViewById(R.id.lay_colorOacity);
        this.controlsShowStkr = findViewById(R.id.controlsShowStkr);
        this.lay_colorOpacity.setOnClickListener(this);
        this.lay_controlStkr.setOnClickListener(this);
        showFragment();
    }

    private void StickerCategoryVertical() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        StickerFragment stickerFragment = (StickerFragment) supportFragmentManager.findFragmentByTag("sticker_main");
        if (stickerFragment != null) {
            beginTransaction.remove(stickerFragment);
        }
        StickerFragment newInstance = StickerFragment.newInstance();
        this.mFragments.add(new WeakReference(newInstance));
        beginTransaction.add(R.id.frameContainerSticker, newInstance, "sticker_main");
        try {
            beginTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void BackgroundCategoryVertical() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        BackgroundFragment backgroundFragment = (BackgroundFragment) supportFragmentManager.findFragmentByTag("inback_category_frgm");
        if (backgroundFragment != null) {
            beginTransaction.remove(backgroundFragment);
        }
        BackgroundFragment newInstance = BackgroundFragment.newInstance();
        this.mFragments.add(new WeakReference(newInstance));
        beginTransaction.add(R.id.frameContainerBackground, newInstance, "inback_category_frgm");
        try {
            beginTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initOverlayRecycler() {
        this.adaptor_overlay = new RecyclerOverLayAdapter(this, Constants.overlayArr, this);
        RecyclerView recyclerView = findViewById(R.id.overlay_recylr);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(this.adaptor_overlay);

    }

    private void showFragment() {
        this.listFragment = new ListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.lay_container, this.listFragment, "fragment").commit();
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_sticker:
                removeScroll();
                removeImageViewControll();
                hideSlideBar();
                if (this.seekbar_container.getVisibility() == View.VISIBLE) {
                    this.seekbar_container.startAnimation(this.animSlideDown);
                    this.seekbar_container.setVisibility(View.GONE);
                    this.lay_sticker.setVisibility(View.GONE);
                }
                if (this.lay_sticker.getVisibility() != View.VISIBLE) {
                    this.lay_sticker.setVisibility(View.VISIBLE);
                    this.img_oK.setVisibility(View.GONE);
                    this.btn_erase.setVisibility(View.GONE);
                    this.btnUndo.setVisibility(View.GONE);
                    this.btnRedo.setVisibility(View.GONE);
                } else {
                    this.lay_sticker.setVisibility(View.GONE);
                    this.img_oK.setVisibility(View.VISIBLE);
                    this.btn_erase.setVisibility(View.VISIBLE);
                    this.btnUndo.setVisibility(View.VISIBLE);
                    this.btnRedo.setVisibility(View.VISIBLE);
                }
                this.lay_effects.setVisibility(View.GONE);
                this.lay_StkrMain.setVisibility(View.GONE);
                this.lay_background.setVisibility(View.GONE);
                this.lay_TextMain.setVisibility(View.GONE);
                addBotton();
                this.txtSticker.setTextColor(getResources().getColor(R.color.color_add_btn));
                return;
            case R.id.add_text:
                removeScroll();
                removeImageViewControll();
                hideSlideBar();
                if (this.seekbar_container.getVisibility() == View.VISIBLE) {
                    this.seekbar_container.startAnimation(this.animSlideDown);
                    this.seekbar_container.setVisibility(View.GONE);
                }
                this.lay_effects.setVisibility(View.GONE);
                this.lay_StkrMain.setVisibility(View.GONE);
                this.lay_background.setVisibility(View.GONE);
                this.lay_TextMain.setVisibility(View.GONE);
                this.lay_sticker.setVisibility(View.GONE);
                addBotton();
                this.txtText.setTextColor(getResources().getColor(R.color.color_add_btn));
                addTextDialog(null);
                return;
            case R.id.btnAlignMentFont:
                setLeftAlignMent();
                return;
            case R.id.btnBoldFont:
                setBoldFonts();
                return;
            case R.id.btnCapitalFont:
                setCapitalFont();
                return;
            case R.id.btnCenterFont:
                setCenterAlignMent();
                return;
            case R.id.btnColorBackgroundPic:
                colorPickerDialog(false);
                return;
            case R.id.btnEditControlBg:
                mainControlBgPickerDialog(false);
                return;
            case R.id.btnEditControlColor:
                mainControlcolorPickerDialog(false);
                return;
            case R.id.btnEditControlOutlineColor:
                mainControlOutlinePickerDialog(false);
                return;
            case R.id.btnEditControlShadowColor:
                mainControlShadowPickerDialog(false);
                return;
            case R.id.btnImgBackground:
            case R.id.btnTakePicture:
                requestGalleryImagePermission();
                return;
            case R.id.btnImgCameraSticker:
                requestGalleryPermission();
                return;
            case R.id.btnItalicFont:
                setItalicFont();
                return;
            case R.id.btnLayoutEffect:
                this.layoutFilterView.setVisibility(View.GONE);
                this.layoutEffectView.setVisibility(View.VISIBLE);
                this.txtEffectText.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
                this.txtFilterText.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
                return;
            case R.id.btnLayoutFilter:
                this.layoutEffectView.setVisibility(View.GONE);
                this.layoutFilterView.setVisibility(View.VISIBLE);
                this.txtEffectText.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
                this.txtFilterText.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
                return;
            case R.id.btnRightFont:
                setRightAlignMent();
                return;
            case R.id.btnShadowBottom:
                setBottomShadow();
                return;
            case R.id.btnShadowLeft:
                setLeftShadow();
                return;
            case R.id.btnShadowRight:
                setRightShadow();
                return;
            case R.id.btnShadowTop:
                setTopShadow();
                return;
            case R.id.btnUnderlineFont:
                setUnderLineFont();
                return;
            case R.id.btn_bck1:
                this.lay_scroll.smoothScrollTo(0, this.distanceScroll);
                return;
            case R.id.btn_bckprass:
                removeScroll();
                onBackPressed();
                return;
            case R.id.btn_done:
                removeScroll();
                hideSlideBar();
                this.lay_effects.setVisibility(View.GONE);
                this.lay_StkrMain.setVisibility(View.GONE);
                this.lay_background.setVisibility(View.GONE);
                this.lay_TextMain.setVisibility(View.GONE);
                this.lay_sticker.setVisibility(View.GONE);
                removeImageViewControll();
                if (this.seekbar_container.getVisibility() == View.VISIBLE) {
                    this.seekbar_container.startAnimation(this.animSlideDown);
                    this.seekbar_container.setVisibility(View.GONE);
                }
                if (this.lay_TextMain.getVisibility() == View.VISIBLE) {
                    this.lay_TextMain.startAnimation(this.animSlideDown);
                    this.lay_TextMain.setVisibility(View.GONE);
                }
                if (this.lay_StkrMain.getVisibility() == View.VISIBLE) {
                    this.lay_StkrMain.startAnimation(this.animSlideDown);
                    this.lay_StkrMain.setVisibility(View.GONE);
                }
                this.guideline.setVisibility(View.GONE);
                this.bitmap = viewToBitmap(this.main_rel);
                String[] split = this.ratio.split(":");
                int parseInt = Integer.parseInt(split[0]);
                int parseInt2 = Integer.parseInt(split[1]);
                if (this.ratio.equals("16:9") || this.ratio.equals("1:1") || this.ratio.equals("9:16") || this.ratio.equals("4:3") || this.ratio.equals("3:4") || this.ratio.equals("2:3")) {
                    Bitmap bitmap2 = this.bitmap;
                    this.bitmap = Bitmap.createScaledBitmap(bitmap2, bitmap2.getWidth() * 2, this.bitmap.getHeight() * 2, true);
                } else {
                    this.bitmap = Bitmap.createScaledBitmap(this.bitmap, parseInt, parseInt2, true);
                }
                requestStoragePermission();
                return;
            case R.id.btn_erase:
                int childCount = txtStkrRel.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = txtStkrRel.getChildAt(i);
                    if (childAt instanceof StickerView) {
                        StickerView stickerView = (StickerView) childAt;
                        if (stickerView.getBorderVisbilty()) {
                            if (!stickerView.getComponentInfo().getSTKR_PATH().equals("")) {
                                Constants.uri = stickerView.getComponentInfo().getSTKR_PATH();
                            } else if (!stickerView.getComponentInfo().getRES_ID().equals("")) {
                                Constants.rewid = stickerView.getComponentInfo().getRES_ID();
                            } else if (stickerView.getMainImageBitmap() != null) {
                                Constants.bitmapSticker = stickerView.getMainImageBitmap();
                            }
                            try {
                                Intent intent = new Intent(this, StickerRemoveActivity.class);
                                intent.putExtra("id", childAt.getId());
                                startActivityForResult(intent, PointerIconCompat.TYPE_GRAB);
                                return;
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                    }
                }
                Toast.makeText(this, "Select sticker to perform erase operation..", Toast.LENGTH_SHORT).show();
                return;
            case R.id.btn_layControls:
                oneTimeScrollLayer();
                removeScroll();
                removeImageViewControll();
                if (this.lay_TextMain.getVisibility() == View.VISIBLE) {
                    this.lay_TextMain.startAnimation(this.animSlideDown);
                    this.lay_TextMain.setVisibility(View.GONE);
                }
                if (this.lay_StkrMain.getVisibility() == View.VISIBLE) {
                    this.lay_StkrMain.startAnimation(this.animSlideDown);
                    this.lay_StkrMain.setVisibility(View.GONE);
                }
                if (lay_container.getVisibility() == View.GONE) {
                    btn_layControls.setVisibility(View.GONE);
                    this.listFragment.getLayoutChild();
                    lay_container.setVisibility(View.VISIBLE);
                    lay_container.animate().translationX((float) lay_container.getLeft()).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();
                    return;
                }
                lay_container.setVisibility(View.VISIBLE);
                lay_container.animate().translationX((float) (-lay_container.getRight())).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
                new Handler().postDelayed(() -> {
                    ThumbnailActivity.lay_container.setVisibility(View.GONE);
                    ThumbnailActivity.btn_layControls.setVisibility(View.VISIBLE);
                }, 200);
                return;
            case R.id.btn_redo:
                redo();
                return;
            case R.id.btn_undo:
                undo();
                return;
            case R.id.btn_up_down:
                this.focusedCopy = this.focusedView;
                removeScroll();
                this.lay_StkrMain.requestLayout();
                this.lay_StkrMain.postInvalidate();
                if (this.seekbar_container.getVisibility() == View.VISIBLE) {
                    hideResContainer();
                } else {
                    showResContainer();
                }
                return;
            case R.id.btn_up_down1:
                this.focusedCopy = this.focusedView;
                removeScroll();
                this.lay_TextMain.requestLayout();
                this.lay_TextMain.postInvalidate();
                if (this.lay_textEdit.getVisibility() == View.VISIBLE) {
                    hideTextResContainer();
                    return;
                } else {
                    showTextResContainer();
                    return;
                }
            case R.id.center_rel:
            case R.id.lay_remove:
                this.lay_effects.setVisibility(View.GONE);
                this.lay_StkrMain.setVisibility(View.GONE);
                this.guideline.setVisibility(View.GONE);
                this.lay_sticker.setVisibility(View.GONE);
                this.lay_background.setVisibility(View.GONE);
                onTouchApply();
                return;
            case R.id.lay_backgnd_control:
                this.fontsShow.setVisibility(View.GONE);
                this.fontsSpacing.setVisibility(View.GONE);
                this.fontsCurve.setVisibility(View.GONE);
                this.colorShow.setVisibility(View.GONE);
                this.sadowShow.setVisibility(View.GONE);
                this.outlineShow.setVisibility(View.GONE);
                this.bgShow.setVisibility(View.VISIBLE);
                this.controlsShow.setVisibility(View.GONE);
                selectControl8();
                return;
            case R.id.lay_colorOpacity:
                this.lay_colorOacity.setVisibility(View.VISIBLE);
                this.controlsShowStkr.setVisibility(View.GONE);
                this.txtControlText.setTextColor(getResources().getColor(R.color.titlecolorbtn));
                this.txtColorOpacity.setTextColor(getResources().getColor(R.color.crop_selected_color));
                return;
            case R.id.lay_colors_control:
                this.fontsShow.setVisibility(View.GONE);
                this.fontsSpacing.setVisibility(View.GONE);
                this.fontsCurve.setVisibility(View.GONE);
                this.colorShow.setVisibility(View.VISIBLE);
                this.sadowShow.setVisibility(View.GONE);
                this.outlineShow.setVisibility(View.GONE);
                this.bgShow.setVisibility(View.GONE);
                this.controlsShow.setVisibility(View.GONE);
                selectControl6();
                return;
            case R.id.lay_controlStkr:
                this.lay_colorOacity.setVisibility(View.GONE);
                this.controlsShowStkr.setVisibility(View.VISIBLE);
                this.txtControlText.setTextColor(getResources().getColor(R.color.crop_selected_color));
                this.txtColorOpacity.setTextColor(getResources().getColor(R.color.titlecolorbtn));
                return;
            case R.id.lay_controls_control:
                this.fontsShow.setVisibility(View.GONE);
                this.fontsSpacing.setVisibility(View.GONE);
                this.fontsCurve.setVisibility(View.GONE);
                this.colorShow.setVisibility(View.GONE);
                this.sadowShow.setVisibility(View.GONE);
                this.outlineShow.setVisibility(View.GONE);
                this.bgShow.setVisibility(View.GONE);
                this.controlsShow.setVisibility(View.VISIBLE);
                selectControl1();
                return;
            case R.id.lay_dupliStkr:
                int childCount2 = txtStkrRel.getChildCount();
                for (int i2 = 0; i2 < childCount2; i2++) {
                    View childAt2 = txtStkrRel.getChildAt(i2);
                    if (childAt2 instanceof StickerView) {
                        StickerView stickerView2 = (StickerView) childAt2;
                        if (stickerView2.getBorderVisbilty()) {
                            StickerView stickerView3 = new StickerView(this);
                            stickerView3.setComponentInfo(stickerView2.getComponentInfo());
                            stickerView3.setId(ViewIdGenerator.generateViewId());
                            stickerView3.setViewWH((float) this.main_rel.getWidth(), (float) this.main_rel.getHeight());
                            txtStkrRel.addView(stickerView3);
                            removeImageViewControll();
                            stickerView3.setOnTouchCallbackListener(this);
                            stickerView3.setBorderVisibility(true);
                        }
                    }
                }
                return;
            case R.id.lay_dupliText:
                int childCount3 = txtStkrRel.getChildCount();
                for (int i3 = 0; i3 < childCount3; i3++) {
                    View childAt3 = txtStkrRel.getChildAt(i3);
                    if (childAt3 instanceof AutofitTextRel) {
                        AutofitTextRel autofitTextRel = (AutofitTextRel) childAt3;
                        if (autofitTextRel.getBorderVisibility()) {
                            AutofitTextRel autofitTextRel2 = new AutofitTextRel(this);
                            txtStkrRel.addView(autofitTextRel2);
                            removeImageViewControll();
                            autofitTextRel2.setTextInfo(autofitTextRel.getTextInfo(), false);
                            autofitTextRel2.setId(ViewIdGenerator.generateViewId());
                            autofitTextRel2.setOnTouchCallbackListener(this);
                            autofitTextRel2.setBorderVisibility(true);
                        }
                    }
                }
                return;
            case R.id.lay_edit:
                doubleTabPrass();
                return;
            case R.id.lay_fonts_Curve:
                this.fontsSpacing.setVisibility(View.GONE);
                this.fontsCurve.setVisibility(View.VISIBLE);
                this.fontsShow.setVisibility(View.GONE);
                this.colorShow.setVisibility(View.GONE);
                this.sadowShow.setVisibility(View.GONE);
                this.outlineShow.setVisibility(View.GONE);
                this.bgShow.setVisibility(View.GONE);
                this.controlsShow.setVisibility(View.GONE);
                selectControl5();
                return;
            case R.id.lay_fonts_Spacing:
                this.fontsSpacing.setVisibility(View.VISIBLE);
                this.fontsCurve.setVisibility(View.GONE);
                this.fontsShow.setVisibility(View.GONE);
                this.colorShow.setVisibility(View.GONE);
                this.sadowShow.setVisibility(View.GONE);
                this.outlineShow.setVisibility(View.GONE);
                this.bgShow.setVisibility(View.GONE);
                this.controlsShow.setVisibility(View.GONE);
                selectControl4();
                return;
            case R.id.lay_fonts_control:
                this.fontsShow.setVisibility(View.VISIBLE);
                this.fontsSpacing.setVisibility(View.GONE);
                this.fontsCurve.setVisibility(View.GONE);
                this.colorShow.setVisibility(View.GONE);
                this.sadowShow.setVisibility(View.GONE);
                this.outlineShow.setVisibility(View.GONE);
                this.bgShow.setVisibility(View.GONE);
                this.controlsShow.setVisibility(View.GONE);
                selectControl2();
                return;
            case R.id.lay_fonts_style:
                this.fontsSpacing.setVisibility(View.GONE);
                this.fontsCurve.setVisibility(View.GONE);
                this.fontsShow.setVisibility(View.GONE);
                this.colorShow.setVisibility(View.GONE);
                this.sadowShow.setVisibility(View.GONE);
                this.outlineShow.setVisibility(View.GONE);
                this.bgShow.setVisibility(View.GONE);
                this.controlsShow.setVisibility(View.GONE);
                selectControl3();
                return;
            case R.id.lay_outline_control:
                this.fontsShow.setVisibility(View.GONE);
                this.fontsSpacing.setVisibility(View.GONE);
                this.fontsCurve.setVisibility(View.GONE);
                this.colorShow.setVisibility(View.GONE);
                this.sadowShow.setVisibility(View.GONE);
                this.outlineShow.setVisibility(View.VISIBLE);
                this.bgShow.setVisibility(View.GONE);
                this.controlsShow.setVisibility(View.GONE);
                selectControl9();
                return;
            case R.id.lay_shadow_control:
                this.fontsShow.setVisibility(View.GONE);
                this.fontsSpacing.setVisibility(View.GONE);
                this.fontsCurve.setVisibility(View.GONE);
                this.colorShow.setVisibility(View.GONE);
                this.sadowShow.setVisibility(View.VISIBLE);
                this.outlineShow.setVisibility(View.GONE);
                this.bgShow.setVisibility(View.GONE);
                this.controlsShow.setVisibility(View.GONE);
                selectControl7();
                return;
            case R.id.select_artwork:
                removeScroll();
                removeImageViewControll();
                hideSlideBar();
                this.lay_effects.setVisibility(View.GONE);
                this.lay_StkrMain.setVisibility(View.GONE);
                this.lay_background.setVisibility(View.GONE);
                this.lay_TextMain.setVisibility(View.GONE);
                this.lay_sticker.setVisibility(View.GONE);
                showPicImageDialog();
                addBotton();
                this.txtImage.setTextColor(getResources().getColor(R.color.color_add_btn));
                return;
            case R.id.select_backgnd:
                hideSlideBar();
                if (this.lay_background.getVisibility() != View.VISIBLE) {
                    this.img_oK.setVisibility(View.GONE);
                    this.btn_erase.setVisibility(View.GONE);
                    this.btnUndo.setVisibility(View.GONE);
                    this.btnRedo.setVisibility(View.GONE);
                    this.lay_background.setVisibility(View.VISIBLE);
                } else {
                    this.img_oK.setVisibility(View.VISIBLE);
                    this.btn_erase.setVisibility(View.VISIBLE);
                    this.btnUndo.setVisibility(View.VISIBLE);
                    this.btnRedo.setVisibility(View.VISIBLE);
                    this.lay_background.setVisibility(View.GONE);
                }
                this.lay_effects.setVisibility(View.GONE);
                this.lay_StkrMain.setVisibility(View.GONE);
                this.lay_TextMain.setVisibility(View.GONE);
                this.lay_sticker.setVisibility(View.GONE);
                addBotton();
                this.txtBG.setTextColor(getResources().getColor(R.color.color_add_btn));
                return;
            case R.id.select_effect:
                removeScroll();
                removeImageViewControll();
                hideSlideBar();
                if (this.lay_effects.getVisibility() != View.VISIBLE) {
                    this.lay_effects.setVisibility(View.VISIBLE);
                    this.lay_effects.startAnimation(this.animSlideUp);
                } else {
                    this.lay_effects.setVisibility(View.GONE);
                    this.lay_effects.startAnimation(this.animSlideDown);
                }
                this.lay_StkrMain.setVisibility(View.GONE);
                this.lay_background.setVisibility(View.GONE);
                this.lay_TextMain.setVisibility(View.GONE);
                this.lay_sticker.setVisibility(View.GONE);
                addBotton();
                this.txtEffect.setTextColor(getResources().getColor(R.color.color_add_btn));
                return;
            default:
                return;
        }
    }

    private void requestStoragePermission() {
        Dexter.withContext(this).withPermissions("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    ThumbnailActivity.this.saveBitmap(true);
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    ThumbnailActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(ThumbnailActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }


    public void requestGalleryPermission() {
        Dexter.withContext(this).withPermissions("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    ThumbnailActivity.this.onGalleryButtonClick();
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    ThumbnailActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(ThumbnailActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }

    private void requestGalleryImagePermission() {
        Dexter.withContext(this).withPermissions("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    ThumbnailActivity.this.onGalleryBackground();
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    ThumbnailActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(dexterError -> Toast.makeText(ThumbnailActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show()).onSameThread().check();
    }


    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialogInterface, i) -> {
            dialogInterface.cancel();
            ThumbnailActivity.this.openSettings();
        });
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
        builder.show();
    }


    public void openSettings() {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, 101);
    }

    private void selectControl9() {
        this.txtTextControls.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtFontsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Style.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtColorsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_curve.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Spacing.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtShadowControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtBgControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_outline_control.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
    }


    public void addBotton() {
        this.txtText.setTextColor(-1);
        this.txtSticker.setTextColor(-1);
        this.txtImage.setTextColor(-1);
        this.txtEffect.setTextColor(-1);
        this.txtBG.setTextColor(-1);
    }

    private void setRightShadow() {
        this.leftRightShadow += 4;
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setLeftRightShadow((float) this.leftRightShadow);
                }
            }
        }
    }

    private void setLeftShadow() {
        this.leftRightShadow -= 4;
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setLeftRightShadow((float) this.leftRightShadow);
                }
            }
        }
    }

    private void setBottomShadow() {
        this.topBottomShadow += 4;
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setTopBottomShadow((float) this.topBottomShadow);
                }
            }
        }
    }

    private void setTopShadow() {
        this.topBottomShadow -= 4;
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setTopBottomShadow((float) this.topBottomShadow);
                }
            }
        }
    }

    private void mainControlcolorPickerDialog(boolean z) {
        new AmbilWarnaDialog(this, this.bColor, z, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                ThumbnailActivity.this.updateColor(i);
            }

            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                Log.e(ThumbnailActivity.TAG, "onCancel: ");
            }
        }).show();
    }

    private void mainControlShadowPickerDialog(boolean z) {
        new AmbilWarnaDialog(this, this.bColor, z, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                ThumbnailActivity.this.updateShadow(i);
            }

            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                Log.e(ThumbnailActivity.TAG, "onCancel: ");
            }
        }).show();
    }

    private void mainControlOutlinePickerDialog(boolean z) {
        new AmbilWarnaDialog(this, this.bColor, z, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                ThumbnailActivity.this.updateOutline(i);
            }

            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                Log.e(ThumbnailActivity.TAG, "onCancel: ");
            }
        }).show();
    }

    private void mainControlBgPickerDialog(boolean z) {
        new AmbilWarnaDialog(this, this.bColor, z, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                ThumbnailActivity.this.updateBgColor(i);
            }

            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
                Log.e(ThumbnailActivity.TAG, "onCancel: ");
            }
        }).show();
    }

    private void showResContainer() {
        this.btn_up_down.animate().setDuration(500).start();
        this.btn_up_down.setBackgroundResource(R.drawable.textlib_down);
        this.seekbar_container.setVisibility(View.VISIBLE);
        this.lay_StkrMain.startAnimation(this.animSlideUp);
        this.lay_StkrMain.requestLayout();
        this.lay_StkrMain.postInvalidate();
        this.lay_StkrMain.post(new Runnable() {
            public void run() {
                ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
                thumbnailActivity.stickerScrollView(thumbnailActivity.focusedView);
            }
        });
    }

    private void hideResContainer() {
        this.btn_up_down.animate().setDuration(500).start();
        this.btn_up_down.setBackgroundResource(R.drawable.textlib_up);
        this.seekbar_container.setVisibility(View.GONE);
        this.lay_StkrMain.startAnimation(this.animSlideDown);
        this.lay_StkrMain.requestLayout();
        this.lay_StkrMain.postInvalidate();
        this.lay_StkrMain.post(() -> {
            ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
            thumbnailActivity.stickerScrollView(thumbnailActivity.focusedView);
        });
    }

    private void showTextResContainer() {
        this.btn_up_down1.animate().setDuration(500).start();
        this.btn_up_down1.setBackgroundResource(R.drawable.textlib_down);
        this.lay_textEdit.setVisibility(View.VISIBLE);
        this.lay_TextMain.startAnimation(this.animSlideUp);
        this.lay_TextMain.requestLayout();
        this.lay_TextMain.postInvalidate();
        this.lay_TextMain.post(() -> {
            ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
            thumbnailActivity.stickerScrollView(thumbnailActivity.focusedView);
        });
    }

    private void hideTextResContainer() {
        this.btn_up_down1.animate().setDuration(500).start();
        this.btn_up_down1.setBackgroundResource(R.drawable.textlib_up);
        this.lay_TextMain.startAnimation(this.animSlideDown);
        this.lay_textEdit.setVisibility(View.GONE);
        this.lay_TextMain.requestLayout();
        this.lay_TextMain.postInvalidate();
        this.lay_TextMain.post(() -> {
            ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
            thumbnailActivity.stickerScrollView(thumbnailActivity.focusedView);
        });
    }

    public void stickerScrollView(View view) {
        float f;
        if (view != null) {
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            float width = (float) view.getWidth();
            float height = (float) view.getHeight();
            boolean z = view instanceof StickerView;
            if (z) {
                f = view.getRotation();
            } else {
                f = view.getRotation();
            }
            int[] iArr2 = new int[2];
            this.lay_scroll.getLocationOnScreen(iArr2);
            this.parentY = (float) iArr2[1];
            float x = view.getX();
            float y = view.getY();
            float f2 = this.parentY;
            float f3 = y + f2;
            this.distance = f2 - ((float) ImageUtils.dpToPx(this, 50.0f));
            Matrix matrix = new Matrix();
            RectF rectF = new RectF(x, f3, x + width, f3 + height);
            matrix.postRotate(f, x + (width / 2.0f), f3 + (height / 2.0f));
            matrix.mapRect(rectF);
            int i = iArr[1];
            float max = Math.max(rectF.top, rectF.bottom);
            float scrollY = (float) this.lay_scroll.getScrollY();
            if (scrollY > 0.0f) {
                max -= scrollY;
            }
            int[] iArr3 = new int[2];
            if (z) {
                this.seekbar_container.getLocationOnScreen(iArr3);
            } else {
                this.lay_textEdit.getLocationOnScreen(iArr3);
            }
            float f4 = (float) iArr3[1];
            if (this.parentY + ((float) this.lay_scroll.getHeight()) < max) {
                max = this.parentY + ((float) this.lay_scroll.getHeight());
            }
            if (max > f4) {
                this.distanceScroll = (int) (max - f4);
                int i2 = this.distanceScroll;
                this.dsfc = i2;
                if (((float) i2) < this.distance) {
                    this.lay_scroll.setY((this.parentY - ((float) ImageUtils.dpToPx(this, 50.0f))) - ((float) this.distanceScroll));
                } else {
                    int scrollY2 = this.lay_scroll.getScrollY();
                    this.lay_scroll.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
                    this.lay_scroll.postInvalidate();
                    this.lay_scroll.requestLayout();
                    int i3 = (int) ((max - this.distance) - f4);
                    this.distanceScroll = scrollY2 + i3;
                    this.lay_scroll.getLayoutParams().height = this.lay_scroll.getHeight() - i3;
                    this.lay_scroll.postInvalidate();
                    this.lay_scroll.requestLayout();
                }
                this.lay_scroll.post(new Runnable() {
                    public void run() {
                        if (ThumbnailActivity.this.lay_scroll.getY() < 0.0f) {
                            ThumbnailActivity.this.lay_scroll.setY(0.0f);
                        }
                        ThumbnailActivity.this.btn_bck1.performClick();
                    }
                });
            }
        }
    }


    public void setTextBgTexture(String str) {
        getResources().getIdentifier(str, "drawable", getPackageName());
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setBgDrawable(str);
                    autofitTextRel.setBgAlpha(this.seekBar3.getProgress());
                    this.bgColor = 0;
                    ((AutofitTextRel) txtStkrRel.getChildAt(i)).getTextInfo().setBG_DRAWABLE(str);
                    this.bgDrawable = autofitTextRel.getBgDrawable();
                    this.bgAlpha = this.seekBar3.getProgress();
                }
            }
        }
    }


    public void setTextFonts(String str) {
        this.fontName = str;
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setTextFont(str);
                    saveBitmapUndu();
                }
            }
        }
    }

    private void setLetterApacing() {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.applyLetterSpacing(this.letterSpacing);
                }
            }
        }
    }

    private void setLineApacing() {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.applyLineSpacing(this.lineSpacing);
                }
            }
        }
    }

    private void setBoldFonts() {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setBoldFont();
                }
            }
        }
    }

    private void setCapitalFont() {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setCapitalFont();
                }
            }
        }
    }

    private void setUnderLineFont() {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setUnderLineFont();
                }
            }
        }
    }

    private void setItalicFont() {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setItalicFont();
                }
            }
        }
    }

    private void setLeftAlignMent() {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setLeftAlignMent();
                }
            }
        }
    }

    private void setCenterAlignMent() {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setCenterAlignMent();
                }
            }
        }
    }

    private void setRightAlignMent() {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setRightAlignMent();
                }
            }
        }
    }


    public void setBitmapOverlay(int i) {
        this.lay_filter.setVisibility(View.VISIBLE);
        this.trans_img.setVisibility(View.VISIBLE);
        try {
            this.trans_img.setImageBitmap(BitmapFactory.decodeResource(getResources(), i));
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), i, options2);
            BitmapFactory.Options options3 = new BitmapFactory.Options();
            options3.inSampleSize = ImageUtils.getClosestResampleSize(options2.outWidth, options2.outHeight, this.main_rel.getWidth() < this.main_rel.getHeight() ? this.main_rel.getWidth() : this.main_rel.getHeight());
            options2.inJustDecodeBounds = false;
            this.trans_img.setImageBitmap(BitmapFactory.decodeResource(getResources(), i, options3));
        }
    }


    public void updateColor(int i) {
        int childCount = txtStkrRel.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = txtStkrRel.getChildAt(i2);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setTextColor(i);
                    this.tColor = i;
                    this.textColorSet = i;
                    this.horizontalPicker.setSelectedColor(i);
                    saveBitmapUndu();
                }
            }
            if (childAt instanceof StickerView) {
                StickerView stickerView = (StickerView) childAt;
                if (stickerView.getBorderVisbilty()) {
                    stickerView.setColor(i);
                    this.stkrColorSet = i;
                    this.horizontalPickerColor.setSelectedColor(i);
                    saveBitmapUndu();
                }
            }
        }
    }


    public void updateShadow(int i) {
        int childCount = txtStkrRel.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = txtStkrRel.getChildAt(i2);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setTextShadowColor(i);
                    this.shadowColor = i;
                    saveBitmapUndu();
                }
            }
        }
    }


    public void updateOutline(int i) {
        int childCount = txtStkrRel.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = txtStkrRel.getChildAt(i2);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setTextOutlineColor(i);
                    this.shadowColor = i;
                    saveBitmapUndu();
                }
            }
        }
    }


    public void updateBgColor(int i) {
        int childCount = txtStkrRel.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = txtStkrRel.getChildAt(i2);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    autofitTextRel.setBgAlpha(this.seekBar3.getProgress());
                    autofitTextRel.setBgColor(i);
                    this.bgColor = i;
                    this.bgDrawable = "0";
                    saveBitmapUndu();
                }
            }
        }
    }


    public void updatePositionSticker(String str) {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                if (autofitTextRel.getBorderVisibility()) {
                    if (str.equals("incrX")) {
                        autofitTextRel.incrX();
                    }
                    if (str.equals("decX")) {
                        autofitTextRel.decX();
                    }
                    if (str.equals("incrY")) {
                        autofitTextRel.incrY();
                    }
                    if (str.equals("decY")) {
                        autofitTextRel.decY();
                    }
                }
            }
            if (childAt instanceof StickerView) {
                StickerView stickerView = (StickerView) childAt;
                if (stickerView.getBorderVisbilty()) {
                    if (str.equals("incrX")) {
                        stickerView.incrX();
                    }
                    if (str.equals("decX")) {
                        stickerView.decX();
                    }
                    if (str.equals("incrY")) {
                        stickerView.incrY();
                    }
                    if (str.equals("decY")) {
                        stickerView.decY();
                    }
                }
            }
        }
    }

    private boolean closeViewAll() {
        this.mViewAllFrame.removeAllViews();
        this.mViewAllFrame.setVisibility(View.GONE);
        return false;
    }


    public void saveComponent1(long j, DatabaseHandler databaseHandler) {
        int childCount = txtStkrRel.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = txtStkrRel.getChildAt(i);
            if (childAt instanceof AutofitTextRel) {
                TextInfo textInfo = ((AutofitTextRel) childAt).getTextInfo();
                textInfo.setTEMPLATE_ID((int) j);
                textInfo.setORDER(i);
                textInfo.setTYPE("TEXT");
                databaseHandler.insertTextRow(textInfo);
            } else {
                saveShapeAndSticker(j, i, TYPE_STICKER, databaseHandler);
            }
        }
    }

    public void saveShapeAndSticker(long j, int i, int i2, DatabaseHandler databaseHandler) {
        ElementInfo componentInfo = ((StickerView) txtStkrRel.getChildAt(i)).getComponentInfo();
        componentInfo.setTEMPLATE_ID((int) j);
        componentInfo.setTYPE("STICKER");
        componentInfo.setORDER(i);
        databaseHandler.insertComponentInfoRow(componentInfo);
    }

    public void addTextDialog(final TextInfo originTextInfo) {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.setContentView(R.layout.addtext_dialog);
        dialog.setCancelable(false);
        TextView textView = (TextView) dialog.findViewById(R.id.txtTitle);
        final AutoFitEditText autoFitEditText = (AutoFitEditText) dialog.findViewById(R.id.auto_fit_edit_text);
        Button button = (Button) dialog.findViewById(R.id.btnCancelDialog);
        Button button2 = (Button) dialog.findViewById(R.id.btnAddTextSDialog);
        if (originTextInfo != null) {
            autoFitEditText.setText(originTextInfo.getTEXT());
        } else {
            autoFitEditText.setText("");
        }
        textView.setTypeface(setBoldFont());
        autoFitEditText.setTypeface(setNormalFont());
        button.setTypeface(setNormalFont());
        button2.setTypeface(setNormalFont());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (autoFitEditText.getText().toString().trim().length() > 0) {
                    String replace = autoFitEditText.getText().toString().replace("\n", " ");
                    TextInfo textInfo = new TextInfo();
                    if (ThumbnailActivity.this.editMode) {
                        textInfo.setTEXT(replace);
                        try {
                            if (originTextInfo != null) {
                                textInfo.setFONT_NAME(originTextInfo.getFONT_NAME());
                                textInfo.setTEXT_COLOR(originTextInfo.getTEXT_COLOR());
                                textInfo.setTEXT_ALPHA(originTextInfo.getTEXT_ALPHA());
                                textInfo.setSHADOW_COLOR(originTextInfo.getSHADOW_COLOR());
                                textInfo.setSHADOW_PROG(originTextInfo.getSHADOW_PROG());
                                textInfo.setBG_COLOR(originTextInfo.getBG_COLOR());
                                textInfo.setBG_DRAWABLE(originTextInfo.getBG_DRAWABLE());
                                textInfo.setBG_ALPHA(originTextInfo.getBG_ALPHA());
                                textInfo.setROTATION(originTextInfo.getROTATION());
                                textInfo.setFIELD_TWO("");
                                textInfo.setPOS_X(originTextInfo.getPOS_X());
                                textInfo.setPOS_Y(originTextInfo.getPOS_Y());
                                textInfo.setWIDTH(originTextInfo.getWIDTH());
                                textInfo.setHEIGHT(originTextInfo.getHEIGHT());
                            } else {
                                textInfo.setFONT_NAME(ThumbnailActivity.this.fontName);
                                textInfo.setTEXT_COLOR(ViewCompat.MEASURED_STATE_MASK);
                                textInfo.setTEXT_ALPHA(100);
                                textInfo.setSHADOW_COLOR(ViewCompat.MEASURED_STATE_MASK);
                                textInfo.setSHADOW_PROG(0);
                                textInfo.setBG_COLOR(ViewCompat.MEASURED_STATE_MASK);
                                textInfo.setBG_DRAWABLE("0");
                                textInfo.setBG_ALPHA(0);
                                textInfo.setROTATION(0.0f);
                                textInfo.setFIELD_TWO("");
                                textInfo.setPOS_X((float) ((ThumbnailActivity.txtStkrRel.getWidth() / 2) - ImageUtils.dpToPx(ThumbnailActivity.this, 100.0f)));
                                textInfo.setPOS_Y((float) ((ThumbnailActivity.txtStkrRel.getHeight() / 2) - ImageUtils.dpToPx(ThumbnailActivity.this, 100.0f)));
                                textInfo.setWIDTH(ImageUtils.dpToPx(ThumbnailActivity.this, 200.0f));
                                textInfo.setHEIGHT(ImageUtils.dpToPx(ThumbnailActivity.this, 200.0f));
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            textInfo.setFONT_NAME(ThumbnailActivity.this.fontName);
                            textInfo.setTEXT_COLOR(ViewCompat.MEASURED_STATE_MASK);
                            textInfo.setTEXT_ALPHA(100);
                            textInfo.setSHADOW_COLOR(ViewCompat.MEASURED_STATE_MASK);
                            textInfo.setSHADOW_PROG(0);
                            textInfo.setBG_COLOR(ViewCompat.MEASURED_STATE_MASK);
                            textInfo.setBG_DRAWABLE("0");
                            textInfo.setBG_ALPHA(0);
                            textInfo.setROTATION(0.0f);
                            textInfo.setFIELD_TWO("");
                            textInfo.setPOS_X((float) ((ThumbnailActivity.txtStkrRel.getWidth() / 2) - ImageUtils.dpToPx(ThumbnailActivity.this, 100.0f)));
                            textInfo.setPOS_Y((float) ((ThumbnailActivity.txtStkrRel.getHeight() / 2) - ImageUtils.dpToPx(ThumbnailActivity.this, 100.0f)));
                            textInfo.setWIDTH(ImageUtils.dpToPx(ThumbnailActivity.this, 200.0f));
                            textInfo.setHEIGHT(ImageUtils.dpToPx(ThumbnailActivity.this, 200.0f));
                        }
                        int childCount = ThumbnailActivity.txtStkrRel.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            View childAt = ThumbnailActivity.txtStkrRel.getChildAt(i);
                            if (childAt instanceof AutofitTextRel) {
                                AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                                if (autofitTextRel.getBorderVisibility()) {
                                    autofitTextRel.setTextInfo(textInfo, false);
                                    autofitTextRel.setBorderVisibility(true);
                                    boolean unused = ThumbnailActivity.this.editMode = false;
                                }
                            }
                        }
                    } else {
                        textInfo.setTEXT(replace);
                        textInfo.setFONT_NAME(ThumbnailActivity.this.fontName);
                        textInfo.setTEXT_COLOR(ViewCompat.MEASURED_STATE_MASK);
                        textInfo.setTEXT_ALPHA(100);
                        textInfo.setSHADOW_COLOR(ViewCompat.MEASURED_STATE_MASK);
                        textInfo.setSHADOW_PROG(0);
                        textInfo.setBG_COLOR(ViewCompat.MEASURED_STATE_MASK);
                        textInfo.setBG_DRAWABLE("0");
                        textInfo.setBG_ALPHA(0);
                        textInfo.setROTATION(0.0f);
                        textInfo.setFIELD_TWO("");
                        textInfo.setPOS_X((float) ((ThumbnailActivity.txtStkrRel.getWidth() / 2) - ImageUtils.dpToPx(ThumbnailActivity.this, 100.0f)));
                        textInfo.setPOS_Y((float) ((ThumbnailActivity.txtStkrRel.getHeight() / 2) - ImageUtils.dpToPx(ThumbnailActivity.this, 100.0f)));
                        textInfo.setWIDTH(ImageUtils.dpToPx(ThumbnailActivity.this, 200.0f));
                        textInfo.setHEIGHT(ImageUtils.dpToPx(ThumbnailActivity.this, 200.0f));
                        try {
                            ThumbnailActivity.this.verticalSeekBar.setProgress(100);
                            ThumbnailActivity.this.seekBar_shadow.setProgress(0);
                            ThumbnailActivity.this.seekBar3.setProgress(255);
                            AutofitTextRel autofitTextRel2 = new AutofitTextRel(ThumbnailActivity.this);
                            ThumbnailActivity.txtStkrRel.addView(autofitTextRel2);
                            autofitTextRel2.setTextInfo(textInfo, false);
                            autofitTextRel2.setId(ViewIdGenerator.generateViewId());
                            autofitTextRel2.setOnTouchCallbackListener(ThumbnailActivity.this);
                            autofitTextRel2.setBorderVisibility(true);
                        } catch (ArrayIndexOutOfBoundsException e2) {
                            e2.printStackTrace();
                        }
                    }
                    if (ThumbnailActivity.this.lay_TextMain.getVisibility() == View.GONE) {
                        ThumbnailActivity.this.lay_TextMain.setVisibility(View.VISIBLE);
                        ThumbnailActivity.this.lay_TextMain.startAnimation(ThumbnailActivity.this.animSlideUp);
                    }
                    ThumbnailActivity.this.saveBitmapUndu();
                    dialog.dismiss();
                    return;
                }
                Toast.makeText(ThumbnailActivity.this, "Please enter text here.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }


    public void onTouchApply() {
        removeScroll();
        if (this.lay_StkrMain.getVisibility() == View.VISIBLE) {
            this.lay_StkrMain.startAnimation(this.animSlideDown);
            this.lay_StkrMain.setVisibility(View.GONE);
        }
        if (this.lay_TextMain.getVisibility() == View.VISIBLE) {
            this.lay_TextMain.startAnimation(this.animSlideDown);
            this.lay_TextMain.setVisibility(View.GONE);
        }
        if (this.showtailsSeek) {
            this.lay_handletails.setVisibility(View.VISIBLE);
        }
        if (this.seekbar_container.getVisibility() == View.GONE) {
            this.seekbar_container.clearAnimation();
            this.lay_TextMain.clearAnimation();
            this.seekbar_container.setVisibility(View.VISIBLE);
            this.seekbar_container.startAnimation(this.animSlideUp);
        }
        this.lay_StkrMain.clearAnimation();
        this.lay_TextMain.clearAnimation();
        removeImageViewControll();
        hideSlideBar();
    }

    public void onSnapFilter(int i, int i2, String str) {
        this.lay_sticker.setVisibility(View.GONE);
        btn_layControls.setVisibility(View.VISIBLE);
        if (this.lay_TextMain.getVisibility() == View.VISIBLE) {
            this.lay_TextMain.startAnimation(this.animSlideDown);
            this.lay_TextMain.setVisibility(View.GONE);
        }
        if (i2 == 104) {
            if (str != null) {
                this.isBackground = true;
                Uri fromFile = Uri.fromFile(new File(str));
                Uri fromFile2 = Uri.fromFile(new File(getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                String[] split = this.ratio.split(":");
                int parseInt = Integer.parseInt(split[0]);
                int parseInt2 = Integer.parseInt(split[1]);
                UCrop.Options options2 = new UCrop.Options();
                options2.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options2.setToolbarColor(getResources().getColor(R.color.color_bg));
                options2.setActiveWidgetColor(getResources().getColor(R.color.color_add_btn));
                options2.setFreeStyleCropEnabled(false);
                UCrop.of(fromFile, fromFile2).withOptions(options2).withAspectRatio((float) parseInt, (float) parseInt2).start(this);
            }
        } else if (!str.equals("")) {
            this.img_oK.setVisibility(View.VISIBLE);
            this.btn_erase.setVisibility(View.VISIBLE);
            if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
                getSupportFragmentManager().popBackStack();
            }
            if (i2 == 50) {
                this.color_Type = "white";
            } else {
                this.color_Type = "white";
            }
            addSticker("", str, null);
        }
    }

    public void onSnapFilter(ArrayList<BackgroundImage> arrayList, int i) {
        if (i == 0) {
            seeMoreSticker(arrayList);
        } else {
            seeMore(arrayList);
        }
    }

    private void seeMoreSticker(ArrayList<BackgroundImage> arrayList) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        StickerFragmentMore stickerFragmentMore = (StickerFragmentMore) supportFragmentManager.findFragmentByTag("sticker_list");
        if (stickerFragmentMore != null) {
            beginTransaction.remove(stickerFragmentMore);
        }
        StickerFragmentMore newInstance = StickerFragmentMore.newInstance(arrayList);
        this.mFragments.add(new WeakReference(newInstance));
        beginTransaction.setCustomAnimations(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
        beginTransaction.add(R.id.frameContainerSticker, newInstance, "sticker_list");
        beginTransaction.addToBackStack("sticker_list");
        try {
            beginTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void seeMore(ArrayList<BackgroundImage> arrayList) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        BackgroundFragment2 backgroundFragment2 = (BackgroundFragment2) supportFragmentManager.findFragmentByTag("back_category_frgm_2");
        if (backgroundFragment2 != null) {
            beginTransaction.remove(backgroundFragment2);
        }
        BackgroundFragment2 newInstance = BackgroundFragment2.newInstance(arrayList);
        this.mFragments.add(new WeakReference(newInstance));
        beginTransaction.setCustomAnimations(R.anim.push_left_in_no_alpha, R.anim.push_left_out_no_alpha);
        beginTransaction.add(R.id.frameContainerBackground, newInstance, "back_category_frgm_2");
        beginTransaction.addToBackStack("back_category_frgm_2");
        try {
            beginTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSticker(String str, String str2, Bitmap bitmap2) {
        if (this.lay_StkrMain.getVisibility() == View.GONE) {
            this.lay_StkrMain.setVisibility(View.VISIBLE);
            this.lay_StkrMain.startAnimation(this.animSlideUp);
        }
        if (this.color_Type.equals("white")) {
            this.lay_color.setVisibility(View.VISIBLE);
            this.lay_hue.setVisibility(View.GONE);
        } else {
            this.lay_color.setVisibility(View.GONE);
            this.lay_hue.setVisibility(View.VISIBLE);
        }
        this.hueSeekbar.setProgress(1);
        removeImageViewControll();
        ElementInfo elementInfo = new ElementInfo();
        elementInfo.setPOS_X((float) ((this.main_rel.getWidth() / 2) - ImageUtils.dpToPx(this, 70.0f)));
        elementInfo.setPOS_Y((float) ((this.main_rel.getHeight() / 2) - ImageUtils.dpToPx(this, 70.0f)));
        elementInfo.setWIDTH(ImageUtils.dpToPx(this, 140.0f));
        elementInfo.setHEIGHT(ImageUtils.dpToPx(this, 140.0f));
        elementInfo.setROTATION(0.0f);
        elementInfo.setRES_ID(str);
        elementInfo.setBITMAP(bitmap2);
        elementInfo.setCOLORTYPE(this.color_Type);
        elementInfo.setTYPE("STICKER");
        elementInfo.setSTC_OPACITY(255);
        elementInfo.setSTC_COLOR(0);
        elementInfo.setSTKR_PATH(str2);
        elementInfo.setSTC_HUE(this.hueSeekbar.getProgress());
        elementInfo.setFIELD_TWO("0,0");
        StickerView stickerView = new StickerView(this);
        stickerView.optimizeScreen(this.screenWidth, this.screenHeight);
        stickerView.setViewWH((float) this.main_rel.getWidth(), (float) this.main_rel.getHeight());
        stickerView.setComponentInfo(elementInfo);
        stickerView.setId(ViewIdGenerator.generateViewId());
        txtStkrRel.addView(stickerView);
        stickerView.setOnTouchCallbackListener(this);
        stickerView.setBorderVisibility(true);
        if (this.seekbar_container.getVisibility() == View.GONE) {
            this.seekbar_container.setVisibility(View.VISIBLE);
            this.seekbar_container.startAnimation(this.animSlideUp);
        }
    }

    private Bitmap viewToBitmap(View view) {
        try {
            Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
            return createBitmap;
        } finally {
            view.destroyDrawingCache();
        }
    }


    public void saveBitmap(final boolean z) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.plzwait));
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(() -> {
            String str;
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "/Thumbnail Design");
                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Log.d("", "Can't create directory to save image.");
                        Toast.makeText(ThumbnailActivity.this.getApplicationContext(), ThumbnailActivity.this.getResources().getString(R.string.create_dir_err), Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                String str2 = "Photo_" + System.currentTimeMillis();
                if (z) {
                    str = str2 + ".png";
                } else {
                    str = str2 + ".jpg";
                }
                ThumbnailActivity.this.filename = file.getPath() + File.separator + str;
                File file2 = new File(ThumbnailActivity.this.filename);
                try {
                    if (!file2.exists()) {
                        file2.createNewFile();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    Bitmap createBitmap = Bitmap.createBitmap(ThumbnailActivity.this.bitmap.getWidth(), ThumbnailActivity.this.bitmap.getHeight(), ThumbnailActivity.this.bitmap.getConfig());
                    Canvas canvas = new Canvas(createBitmap);
                    canvas.drawColor(-1);
                    canvas.drawBitmap(ThumbnailActivity.this.bitmap, 0.0f, 0.0f, null);
                    ThumbnailActivity.this.checkMemory = createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    createBitmap.recycle();
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    ThumbnailActivity.isUpadted = true;
                    MediaScannerConnection.scanFile(ThumbnailActivity.this, new String[]{file2.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String str, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + str + ":");
                            StringBuilder sb = new StringBuilder();
                            sb.append("-> uri=");
                            sb.append(uri);
                            Log.i("ExternalStorage", sb.toString());
                        }
                    });
                    ThumbnailActivity.this.sendBroadcast(new Intent("androR.id.intent.action.MEDIA_SCANNER_SCAN_FILE", FileProvider.getUriForFile(ThumbnailActivity.this, ThumbnailActivity.this.getApplicationContext().getPackageName() + ".provider", file2)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(1000);
                progressDialog.dismiss();
            } catch (Exception e2) {
            }
        }).start();
        progressDialog.setOnDismissListener(dialogInterface -> {
            if (ThumbnailActivity.this.checkMemory) {
                ThumbnailActivity.this.main_rel.setDrawingCacheEnabled(true);
                Bitmap createBitmap = Bitmap.createBitmap(ThumbnailActivity.this.main_rel.getDrawingCache());
                ThumbnailActivity.this.main_rel.setDrawingCacheEnabled(false);
                DatabaseHandler databaseHandler = null;
                try {
                    if (ThumbnailActivity.this.ratio.equals("")) {
                        ThumbnailActivity.this.temp_path = Constants.saveBitmapObject1(ThumbnailActivity.imgBtmap);
                    }
                    ThumbnailActivity.this.temp_path = Constants.saveBitmapObject1(ThumbnailActivity.imgBtmap);
                    String saveBitmapObject = Constants.saveBitmapObject(ThumbnailActivity.activity, ImageUtils.resizeBitmap(createBitmap, ((int) ThumbnailActivity.this.screenWidth) / 2, ((int) ThumbnailActivity.this.screenHeight) / 2));
                    if (saveBitmapObject != null) {
                        TemplateInfo templateInfo = new TemplateInfo();
                        templateInfo.setTHUMB_URI(saveBitmapObject);
                        templateInfo.setFRAME_NAME(ThumbnailActivity.this.frame_Name);
                        templateInfo.setRATIO(ThumbnailActivity.this.ratio);
                        templateInfo.setPROFILE_TYPE(ThumbnailActivity.this.profile);
                        templateInfo.setSEEK_VALUE(String.valueOf(ThumbnailActivity.this.seekValue));
                        templateInfo.setTYPE("USER");
                        templateInfo.setTEMP_PATH(ThumbnailActivity.this.temp_path);
                        templateInfo.setTEMPCOLOR(ThumbnailActivity.this.hex);
                        templateInfo.setOVERLAY_NAME(ThumbnailActivity.this.overlay_Name);
                        templateInfo.setOVERLAY_OPACITY(ThumbnailActivity.this.seek.getProgress());
                        templateInfo.setOVERLAY_BLUR(ThumbnailActivity.this.seek_blur.getProgress());
                        databaseHandler = DatabaseHandler.getDbHandler(ThumbnailActivity.this.getApplicationContext());
                        ThumbnailActivity.this.saveComponent1(databaseHandler.insertTemplateRow(templateInfo), databaseHandler);
                        ThumbnailActivity.isUpdated = true;
                    }
                    if (databaseHandler != null) {
                        databaseHandler.close();
                    }
                } catch (Exception e) {
                    Log.i("testing", "Exception " + e.getMessage());
                    e.printStackTrace();
                    if (databaseHandler != null) {
                        databaseHandler.close();
                    }
                } catch (Throwable th) {
                    if (databaseHandler != null) {
                        databaseHandler.close();
                    }
                }
                ThumbnailActivity.this.imageSavedSuccess();
                return;
            }
            new AlertDialog.Builder(ThumbnailActivity.this, 16974126).setMessage(Constants.getSpannableString(ThumbnailActivity.this, Typeface.DEFAULT, R.string.memoryerror)).setPositiveButton(Constants.getSpannableString(ThumbnailActivity.this, Typeface.DEFAULT, R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        });
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        int id = seekBar.getId();
        int i2 = 0;
        if (id == R.id.alpha_seekBar) {
            int childCount = txtStkrRel.getChildCount();
            while (i2 < childCount) {
                View childAt = txtStkrRel.getChildAt(i2);
                if (childAt instanceof StickerView) {
                    StickerView stickerView = (StickerView) childAt;
                    if (stickerView.getBorderVisbilty()) {
                        stickerView.setAlphaProg(i);
                    }
                }
                i2++;
            }
        } else if (id != R.id.hue_seekBar) {
            switch (id) {
                case R.id.seek:
                    this.alpha = i;
                    if (Build.VERSION.SDK_INT >= 16) {
                        this.trans_img.setImageAlpha(this.alpha);
                        return;
                    } else {
                        this.trans_img.setAlpha(this.alpha);
                        return;
                    }
                case R.id.seekBar2:
                    this.processs = i;
                    int childCount2 = txtStkrRel.getChildCount();
                    while (i2 < childCount2) {
                        View childAt2 = txtStkrRel.getChildAt(i2);
                        if (childAt2 instanceof AutofitTextRel) {
                            AutofitTextRel autofitTextRel = (AutofitTextRel) childAt2;
                            if (autofitTextRel.getBorderVisibility()) {
                                autofitTextRel.setTextAlpha(i);
                            }
                        }
                        i2++;
                    }
                    return;
                case R.id.seekBar3:
                    int childCount3 = txtStkrRel.getChildCount();
                    while (i2 < childCount3) {
                        View childAt3 = txtStkrRel.getChildAt(i2);
                        if (childAt3 instanceof AutofitTextRel) {
                            AutofitTextRel autofitTextRel2 = (AutofitTextRel) childAt3;
                            if (autofitTextRel2.getBorderVisibility()) {
                                autofitTextRel2.setBgAlpha(i);
                                this.bgAlpha = i;
                            }
                        }
                        i2++;
                    }
                    return;
                case R.id.seekBar_shadow:
                    int childCount4 = txtStkrRel.getChildCount();
                    while (i2 < childCount4) {
                        View childAt4 = txtStkrRel.getChildAt(i2);
                        if (childAt4 instanceof AutofitTextRel) {
                            AutofitTextRel autofitTextRel3 = (AutofitTextRel) childAt4;
                            if (autofitTextRel3.getBorderVisibility()) {
                                autofitTextRel3.setTextShadowProg(i);
                                this.shadowProg = i;
                            }
                        }
                        i2++;
                    }
                    return;
                case R.id.seekLetterSpacing:
                    this.letterSpacing = (float) (i / 3);
                    setLetterApacing();
                    return;
                case R.id.seekLineSpacing:
                    this.lineSpacing = (float) (i / 2);
                    setLineApacing();
                    return;
                case R.id.seekOutlineSize:
                    int childCount5 = txtStkrRel.getChildCount();
                    while (i2 < childCount5) {
                        View childAt5 = txtStkrRel.getChildAt(i2);
                        if (childAt5 instanceof AutofitTextRel) {
                            AutofitTextRel autofitTextRel4 = (AutofitTextRel) childAt5;
                            if (autofitTextRel4.getBorderVisibility()) {
                                autofitTextRel4.setTextOutlLine(i);
                            }
                        }
                        i2++;
                    }
                    return;
                case R.id.seekShadowBlur:
                    int childCount6 = txtStkrRel.getChildCount();
                    while (i2 < childCount6) {
                        View childAt6 = txtStkrRel.getChildAt(i2);
                        if (childAt6 instanceof AutofitTextRel) {
                            AutofitTextRel autofitTextRel5 = (AutofitTextRel) childAt6;
                            if (autofitTextRel5.getBorderVisibility()) {
                                autofitTextRel5.setTextShadowOpacity(i);
                            }
                        }
                        i2++;
                    }
                    return;
                case R.id.seekTextCurve:
                    mRadius = seekBar.getProgress() - 360;
                    int i3 = mRadius;
                    if (i3 <= 0 && i3 >= -8) {
                        mRadius = -8;
                    }
                    int childCount7 = txtStkrRel.getChildCount();
                    while (i2 < childCount7) {
                        View childAt7 = txtStkrRel.getChildAt(i2);
                        if (childAt7 instanceof AutofitTextRel) {
                            AutofitTextRel autofitTextRel6 = (AutofitTextRel) childAt7;
                            if (autofitTextRel6.getBorderVisibility()) {
                                autofitTextRel6.setDrawParams();
                            }
                        }
                        i2++;
                    }
                    return;
                case R.id.seek_blur:
                    if (i != 0) {
                        this.background_blur.setVisibility(View.VISIBLE);
                        this.min = i;
                        if (Build.VERSION.SDK_INT >= 16) {
                            this.background_blur.setImageAlpha(i);
                            return;
                        } else {
                            this.background_blur.setAlpha(i);
                            return;
                        }
                    } else {
                        this.background_blur.setVisibility(View.GONE);
                        return;
                    }
                case R.id.seek_tailys:
                    this.background_blur.setVisibility(View.GONE);
                    this.seekValue = i;
                    addTilesBG(this.curTileId);
                    return;
                default:
                    return;
            }
        } else {
            int childCount8 = txtStkrRel.getChildCount();
            while (i2 < childCount8) {
                View childAt8 = txtStkrRel.getChildAt(i2);
                if (childAt8 instanceof StickerView) {
                    StickerView stickerView2 = (StickerView) childAt8;
                    if (stickerView2.getBorderVisbilty()) {
                        stickerView2.setHueProg(i);
                    }
                }
                i2++;
            }
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.alpha_seekBar:
            case R.id.hue_seekBar:
            case R.id.seekBar2:
                saveBitmapUndu();
                return;
            case R.id.seek_tailys:
                if (this.min != 0) {
                    this.background_blur.setVisibility(View.VISIBLE);
                    return;
                } else {
                    this.background_blur.setVisibility(View.GONE);
                    return;
                }
            default:
                return;
        }
    }

    private void addTilesBG(int i) {
        if (i != 0) {
            setImageBitmapAndResizeLayout1(Constants.getTiledBitmap(this, i, imgBtmap, seek_tailys));
        }
    }

    private void setImageBitmapAndResizeLayout1(Bitmap bitmap2) {
        this.main_rel.getLayoutParams().width = bitmap2.getWidth();
        this.main_rel.getLayoutParams().height = bitmap2.getHeight();
        this.main_rel.postInvalidate();
        this.main_rel.requestLayout();
        background_img.setImageBitmap(bitmap2);
        imgBtmap = bitmap2;
    }


    public void oneTimeLayerAdjust() {
        if (this.appPreference.getInt(Constants.onTimeRecentHint, 0) == 0) {
            this.appPreference.putInt(Constants.onTimeRecentHint, 1);
            new Handler().postDelayed(() -> Constants.showRecentHindDialog(ThumbnailActivity.btn_layControls, ThumbnailActivity.this), 1000);
        }
    }

    public void oneTimeScrollLayer() {
        if (this.appPreference.getInt(Constants.onTimeLayerScroll, 0) == 0) {
            this.appPreference.putInt(Constants.onTimeLayerScroll, 1);
            new Handler().postDelayed(() -> {
                Constants.showScrollLayerDialog(ListFragment.HintView, ThumbnailActivity.this);
            }, 1000);
        }
    }

    private void touchDown(View view, String str) {
        this.leftRightShadow = 0;
        this.topBottomShadow = 0;
        this.focusedView = view;
        if (str.equals("hideboder")) {
            removeImageViewControll();
        }
        hideSlideBar();
        if (view instanceof StickerView) {
            this.lay_effects.setVisibility(View.GONE);
            this.lay_TextMain.setVisibility(View.GONE);
            this.lay_StkrMain.setVisibility(View.GONE);
            StickerView stickerView = (StickerView) view;
            this.stkrColorSet = stickerView.getColor();
            this.alphaSeekbar.setProgress(stickerView.getAlphaProg());
            this.hueSeekbar.setProgress(stickerView.getHueProg());
        }
        if (view instanceof AutofitTextRel) {
            this.lay_effects.setVisibility(View.GONE);
            this.lay_StkrMain.setVisibility(View.GONE);
            this.lay_TextMain.setVisibility(View.GONE);
        }
        if (this.guideline.getVisibility() == View.GONE) {
            this.guideline.setVisibility(View.VISIBLE);
        }
    }

    private void hideSlideBar() {
        if (lay_container.getVisibility() == View.VISIBLE) {
            lay_container.animate().translationX((float) (-lay_container.getRight())).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
            new Handler().postDelayed(() -> {
                ThumbnailActivity.lay_container.setVisibility(View.GONE);
                ThumbnailActivity.btn_layControls.setVisibility(View.VISIBLE);
            }, 200);
        }
    }

    private void touchMove(View view) {
        boolean z = view instanceof StickerView;
        if (z) {
            StickerView stickerView = (StickerView) view;
            this.alphaSeekbar.setProgress(stickerView.getAlphaProg());
            this.hueSeekbar.setProgress(stickerView.getHueProg());
        } else {
            this.lay_TextMain.setVisibility(View.GONE);
        }
        if (z) {
            this.lay_effects.setVisibility(View.GONE);
            this.lay_TextMain.setVisibility(View.GONE);
            this.lay_StkrMain.setVisibility(View.GONE);
        }
        if (view instanceof AutofitTextRel) {
            this.lay_effects.setVisibility(View.GONE);
            this.lay_TextMain.setVisibility(View.GONE);
            this.lay_StkrMain.setVisibility(View.GONE);
        }
    }

    private void touchUp(final View view) {
        if (this.focusedCopy != this.focusedView) {
            this.seekbar_container.setVisibility(View.VISIBLE);
            this.lay_textEdit.setVisibility(View.VISIBLE);
        }
        if (view instanceof AutofitTextRel) {
            this.rotation = view.getRotation();
            if (this.lay_TextMain.getVisibility() == View.GONE) {
                this.lay_TextMain.setVisibility(View.VISIBLE);
                this.lay_TextMain.startAnimation(this.animSlideUp);
                this.lay_TextMain.post(() -> ThumbnailActivity.this.stickerScrollView(view));
            }
            int i = this.processs;
            if (i != 0) {
                this.verticalSeekBar.setProgress(i);
            }
        }
        if ((view instanceof StickerView) && this.lay_StkrMain.getVisibility() == View.GONE) {
            if (("" + ((StickerView) view).getColorType()).equals("white")) {
                this.lay_color.setVisibility(View.VISIBLE);
                this.lay_hue.setVisibility(View.GONE);
            } else {
                this.lay_color.setVisibility(View.GONE);
                this.lay_hue.setVisibility(View.VISIBLE);
            }
            this.lay_StkrMain.setVisibility(View.VISIBLE);
            this.lay_StkrMain.startAnimation(this.animSlideUp);
            this.lay_StkrMain.post(() -> ThumbnailActivity.this.stickerScrollView(view));
        }
        if (this.guideline.getVisibility() == View.VISIBLE) {
            this.guideline.setVisibility(View.GONE);
        }
        if (this.seekbar_container.getVisibility() == View.GONE) {
            this.seekbar_container.startAnimation(this.animSlideDown);
            this.seekbar_container.setVisibility(View.GONE);
        }
    }

    public void onDelete() {
        removeScroll();
        if (this.lay_StkrMain.getVisibility() == View.VISIBLE) {
            this.lay_StkrMain.startAnimation(this.animSlideDown);
            this.lay_StkrMain.setVisibility(View.GONE);
        }
        if (this.lay_TextMain.getVisibility() == View.GONE) {
            this.lay_TextMain.startAnimation(this.animSlideDown);
            this.lay_TextMain.setVisibility(View.GONE);
        }
        this.guideline.setVisibility(View.GONE);
        saveBitmapUndu();
    }

    public void onRotateDown(View view) {
        touchDown(view, "viewboder");
    }

    public void onRotateMove(View view) {
        touchMove(view);
    }

    public void onRotateUp(View view) {
        touchUp(view);
    }

    public void onScaleDown(View view) {
        touchDown(view, "viewboder");
    }

    public void onScaleMove(View view) {
        touchMove(view);
    }

    public void onScaleUp(View view) {
        touchUp(view);
    }

    public void onTouchDown(View view) {
        touchDown(view, "hideboder");
        if (this.checkTouchContinue) {
            this.lay_StkrMain.post(() -> {
                ThumbnailActivity.this.checkTouchContinue = true;
                ThumbnailActivity.this.mHandler.post(ThumbnailActivity.this.mStatusChecker);
            });
        }
    }

    public void onTouchMove(View view) {
        touchMove(view);
    }

    public void onTouchUp(View view) {
        this.checkTouchContinue = false;
        this.mHandler.removeCallbacks(this.mStatusChecker);
        touchUp(view);
    }

    public void onTouchMoveUpClick(View view) {
        saveBitmapUndu();
    }

    public void onDoubleTap() {
        doubleTabPrass();
    }

    private void doubleTabPrass() {
        this.editMode = true;
        try {
            if (txtStkrRel.getChildAt(txtStkrRel.getChildCount() - 1) instanceof AutofitTextRel) {
                TextInfo textInfo = ((AutofitTextRel) txtStkrRel.getChildAt(txtStkrRel.getChildCount() - 1)).getTextInfo();
                this.lay_effects.setVisibility(View.GONE);
                this.lay_StkrMain.setVisibility(View.GONE);
                this.lay_TextMain.setVisibility(View.GONE);
                addTextDialog(textInfo);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void removeScroll() {
        int[] iArr = new int[2];
        this.lay_scroll.getLocationOnScreen(iArr);
        final float f = (float) iArr[1];
        final float dpToPx = (float) ImageUtils.dpToPx(this, 50.0f);
        if (f != dpToPx) {
            this.lay_scroll.setY(this.yAtLayoutCenter - ((float) ImageUtils.dpToPx(this, 50.0f)));
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
        layoutParams.addRule(13);
        this.lay_scroll.setLayoutParams(layoutParams);
        this.lay_scroll.postInvalidate();
        this.lay_scroll.requestLayout();
        this.lay_scroll.post(() -> {
            if (f != dpToPx) {
                ThumbnailActivity.this.lay_scroll.setY(ThumbnailActivity.this.yAtLayoutCenter - ((float) ImageUtils.dpToPx(ThumbnailActivity.this, 50.0f)));
            }
        });
    }

    public void removeImageViewControll() {
        this.guideline.setVisibility(View.GONE);
        RelativeLayout relativeLayout = txtStkrRel;
        if (relativeLayout != null) {
            int childCount = relativeLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = txtStkrRel.getChildAt(i);
                if (childAt instanceof AutofitTextRel) {
                    ((AutofitTextRel) childAt).setBorderVisibility(false);
                }
                if (childAt instanceof StickerView) {
                    Log.e("remove", "==");
                    ((StickerView) childAt).setBorderVisibility(false);
                }
            }
        }
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        int i3 = i;
        int i4 = i2;
        Intent intent2 = intent;
        super.onActivityResult(i, i2, intent);
        if (i4 == -1) {
            this.lay_StkrMain.setVisibility(View.GONE);
            if (intent2 != null || i3 == SELECT_PICTURE_FROM_CAMERA || i3 == 4 || i3 == TEXT_ACTIVITY) {
                Bundle bundle = null;
                if (i3 == TEXT_ACTIVITY) {
                    bundle = intent.getExtras();
                    TextInfo textInfo = new TextInfo();
                    textInfo.setPOS_X(bundle.getFloat("X", 0.0f));
                    textInfo.setPOS_Y(bundle.getFloat("Y", 0.0f));
                    textInfo.setWIDTH(bundle.getInt("wi", ImageUtils.dpToPx(this, 200.0f)));
                    textInfo.setHEIGHT(bundle.getInt("he", ImageUtils.dpToPx(this, 200.0f)));
                    textInfo.setTEXT(bundle.getString("text", ""));
                    textInfo.setFONT_NAME(bundle.getString("fontName", ""));
                    textInfo.setTEXT_COLOR(bundle.getInt("tColor", Color.parseColor("#4149b6")));
                    textInfo.setTEXT_ALPHA(bundle.getInt("tAlpha", 100));
                    textInfo.setSHADOW_COLOR(bundle.getInt("shadowColor", Color.parseColor("#7641b6")));
                    textInfo.setSHADOW_PROG(bundle.getInt("shadowProg", 5));
                    textInfo.setBG_COLOR(bundle.getInt("bgColor", 0));
                    textInfo.setBG_DRAWABLE(bundle.getString("bgDrawable", "0"));
                    textInfo.setBG_ALPHA(bundle.getInt("bgAlpha", 255));
                    textInfo.setROTATION(bundle.getFloat("rotation", 0.0f));
                    textInfo.setFIELD_TWO(bundle.getString("field_two", ""));
                    Log.e("double tab 22", "" + bundle.getFloat("X", 0.0f) + " ," + bundle.getFloat("Y", 0.0f));
                    this.fontName = bundle.getString("fontName", "");
                    this.tColor = bundle.getInt("tColor", Color.parseColor("#4149b6"));
                    this.shadowColor = bundle.getInt("shadowColor", Color.parseColor("#7641b6"));
                    this.shadowProg = bundle.getInt("shadowProg", 0);
                    this.tAlpha = bundle.getInt("tAlpha", 100);
                    this.bgDrawable = bundle.getString("bgDrawable", "0");
                    this.bgAlpha = bundle.getInt("bgAlpha", 255);
                    this.rotation = bundle.getFloat("rotation", 0.0f);
                    this.bgColor = bundle.getInt("bgColor", 0);
                    if (this.editMode) {
                        RelativeLayout relativeLayout = txtStkrRel;
                        ((AutofitTextRel) relativeLayout.getChildAt(relativeLayout.getChildCount() - 1)).setTextInfo(textInfo, false);
                        RelativeLayout relativeLayout2 = txtStkrRel;
                        ((AutofitTextRel) relativeLayout2.getChildAt(relativeLayout2.getChildCount() - 1)).setBorderVisibility(true);
                        this.editMode = false;
                    } else {
                        this.verticalSeekBar.setProgress(100);
                        this.seekBar_shadow.setProgress(0);
                        this.seekBar3.setProgress(255);
                        AutofitTextRel autofitTextRel = new AutofitTextRel(this);
                        txtStkrRel.addView(autofitTextRel);
                        autofitTextRel.setTextInfo(textInfo, false);
                        autofitTextRel.setId(ViewIdGenerator.generateViewId());
                        autofitTextRel.setOnTouchCallbackListener(this);
                        autofitTextRel.setBorderVisibility(true);
                    }
                    if (this.lay_TextMain.getVisibility() == View.GONE) {
                        this.lay_TextMain.setVisibility(View.VISIBLE);
                        this.lay_TextMain.startAnimation(this.animSlideUp);
                    }
                }
                if (i3 == 1020) {
                    int childCount = txtStkrRel.getChildCount();
                    int i5 = intent.getExtras().getInt("id");
                    for (int i6 = 0; i6 < childCount; i6++) {
                        View childAt = txtStkrRel.getChildAt(i6);
                        if ((childAt instanceof StickerView) && childAt.getId() == i5) {
                            ((StickerView) childAt).setStrPath(intent.getExtras().getString(ClientCookie.PATH_ATTR));
                            saveBitmapUndu();
                        }
                    }
                }
                if (i3 == SELECT_PICTURE_FROM_GALLERY) {
                    try {
                        Uri fromFile = Uri.fromFile(new File(getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                        UCrop.Options options2 = new UCrop.Options();
                        options2.setCompressionFormat(Bitmap.CompressFormat.PNG);
                        options2.setToolbarColor(getResources().getColor(R.color.color_bg));
                        options2.setActiveWidgetColor(getResources().getColor(R.color.color_add_btn));
                        options2.setFreeStyleCropEnabled(true);
                        UCrop.of(intent.getData(), fromFile).withOptions(options2).start(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (i3 == SELECT_PICTURE_FROM_CAMERA) {
                    try {
                        Uri fromFile2 = Uri.fromFile(new File(getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png"));
                        UCrop.Options options3 = new UCrop.Options();
                        options3.setCompressionFormat(Bitmap.CompressFormat.PNG);
                        options3.setToolbarColor(getResources().getColor(R.color.color_bg));
                        options3.setActiveWidgetColor(getResources().getColor(R.color.color_add_btn));
                        options3.setFreeStyleCropEnabled(true);
                        UCrop.of(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", this.file), fromFile2).withOptions(options3).start(this);
                    } catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                if (i3 == SELECT_PICTURE_FROM_GALLERY_BACKGROUND) {
                    try {
                        this.lay_background.setVisibility(View.GONE);
                        this.img_oK.setVisibility(View.VISIBLE);
                        this.btn_erase.setVisibility(View.VISIBLE);
                        if (this.lay_background.getVisibility() == View.VISIBLE) {
                            this.lay_background.startAnimation(this.animSlideDown);
                            this.lay_background.setVisibility(View.GONE);
                        }
                        this.screenWidth = (float) background_img.getWidth();
                        this.screenHeight = (float) background_img.getHeight();
                        Constants.bitmap = ImageUtils.scaleCenterCrop(Constants.getBitmapFromUri(this, intent.getData(), this.screenWidth, this.screenHeight), (int) this.screenHeight, (int) this.screenWidth);
                        this.showtailsSeek = false;
                        this.position = "1";
                        this.profile = "Temp_Path";
                        this.hex = "";
                        setImageBitmapAndResizeLayout(Constants.bitmap, "nonCreated");
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
                if (i4 == -1 && i3 == 69) {
                    handleCropResult(intent2);
                } else if (i4 == 96) {
                    UCrop.getError(intent);
                }
                if (i3 == 4) {
                    openCustomActivity(bundle, intent2);
                    return;
                }
                return;
            }
            new AlertDialog.Builder(this, 16974126).setMessage(Constants.getSpannableString(this, Typeface.DEFAULT, R.string.picUpImg)).setPositiveButton(Constants.getSpannableString(this, Typeface.DEFAULT, R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            }).create().show();
        } else if (i3 == TEXT_ACTIVITY) {
            this.editMode = false;
        }
    }

    private void handleCropResult(@NonNull Intent intent) {
        this.lay_background.setVisibility(View.GONE);
        this.img_oK.setVisibility(View.VISIBLE);
        this.btn_erase.setVisibility(View.VISIBLE);
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            getSupportFragmentManager().popBackStack();
        }
        Uri output = UCrop.getOutput(intent);
        if (this.isBackground) {
            this.profile = "no";
            if (this.profile.equals("no")) {
                this.showtailsSeek = false;
                this.position = "1";
                this.profile = "Temp_Path";
                this.hex = "";
                try {
                    if (this.seekbar_container.getVisibility() == View.GONE) {
                        this.seekbar_container.setVisibility(View.VISIBLE);
                        this.seekbar_container.startAnimation(this.animSlideUp);
                    }
                    bitmapRatio(this.ratio, this.profile, ImageUtils.getResampleImageBitmap(output, this, (int) (this.screenWidth > this.screenHeight ? this.screenWidth : this.screenHeight)), "nonCreated");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            this.color_Type = "colored";
            addSticker("", output.getPath(), null);
        }
        this.isBackground = false;
    }

    private int gcd(int i, int i2) {
        return i2 == 0 ? i : gcd(i2, i % i2);
    }

    public void openCustomActivity(Bundle bundle, Intent intent) {
        Bundle extras = intent.getExtras();
        this.profile = "no";
        if (this.profile.equals("no")) {
            this.showtailsSeek = false;
            this.position = "1";
            this.profile = "Temp_Path";
            this.hex = "";
            setImageBitmapAndResizeLayout(ImageUtils.resizeBitmap(Constants.bitmap, (int) this.screenWidth, (int) this.screenHeight), "nonCreated");
            return;
        }
        if (this.profile.equals("Texture")) {
            this.showtailsSeek = true;
            this.lay_handletails.setVisibility(View.VISIBLE);
        } else {
            this.showtailsSeek = false;
            this.lay_handletails.setVisibility(View.GONE);
        }
        String string = extras.getString("position");
        this.hex = extras.getString("color");
        drawBackgroundImageFromDp(this.ratio, string, this.profile, "nonCreated");
    }

    public void onGalleryButtonClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.PICK");
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), SELECT_PICTURE_FROM_GALLERY);
    }

    public void onCameraButtonClick() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        this.file = new File(Environment.getExternalStorageDirectory(), ".temp.jpg");
        intent.putExtra("output", FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", this.file));
        startActivityForResult(intent, SELECT_PICTURE_FROM_CAMERA);
    }

    public void onGalleryBackground() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), SELECT_PICTURE_FROM_GALLERY_BACKGROUND);
    }

    private void colorPickerDialog(boolean z) {
        new AmbilWarnaDialog(this, this.bColor, z, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            public void onCancel(AmbilWarnaDialog ambilWarnaDialog) {
            }

            public void onOk(AmbilWarnaDialog ambilWarnaDialog, int i) {
                ThumbnailActivity.this.updateBackgroundColor(i);
            }
        }).show();
    }


    public void updateBackgroundColor(int i) {
        this.lay_background.setVisibility(View.GONE);
        this.img_oK.setVisibility(View.VISIBLE);
        this.btn_erase.setVisibility(View.VISIBLE);
        if (this.lay_background.getVisibility() == View.VISIBLE) {
            this.lay_background.startAnimation(this.animSlideDown);
            this.lay_background.setVisibility(View.GONE);
        }
        Bitmap createBitmap = Bitmap.createBitmap(720, 1280, Bitmap.Config.ARGB_8888);
        createBitmap.eraseColor(i);
        Log.e(TAG, "updateColor: ");
        try {
            this.screenWidth = (float) background_img.getWidth();
            this.screenHeight = (float) background_img.getHeight();
            Constants.bitmap = ImageUtils.scaleCenterCrop(createBitmap, (int) this.screenHeight, (int) this.screenWidth);
            this.showtailsSeek = false;
            this.position = "1";
            this.profile = "Temp_Path";
            this.hex = "";
            setImageBitmapAndResizeLayout(Constants.bitmap, "nonCreated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            getSupportFragmentManager().popBackStack();
        } else if (this.mViewAllFrame.getVisibility() == View.VISIBLE) {
            closeViewAll();
        } else if (this.lay_TextMain.getVisibility() == View.VISIBLE) {
            if (this.lay_textEdit.getVisibility() == View.VISIBLE) {
                hideTextResContainer();
                removeScroll();
                return;
            }
            showBackDialog();
        } else if (this.lay_StkrMain.getVisibility() == View.VISIBLE) {
            if (this.seekbar_container.getVisibility() == View.VISIBLE) {
                hideResContainer();
                removeScroll();
                return;
            }
            showBackDialog();
        } else if (this.lay_sticker.getVisibility() == View.VISIBLE) {
            this.lay_sticker.setVisibility(View.GONE);
            this.img_oK.setVisibility(View.VISIBLE);
            this.btn_erase.setVisibility(View.VISIBLE);
            this.btnUndo.setVisibility(View.VISIBLE);
            this.btnRedo.setVisibility(View.VISIBLE);
            btn_layControls.setVisibility(View.VISIBLE);
        } else if (this.seekbar_container.getVisibility() == View.VISIBLE) {
            this.seekbar_container.startAnimation(this.animSlideDown);
            this.seekbar_container.setVisibility(View.GONE);
        } else if (this.lay_effects.getVisibility() == View.VISIBLE) {
            this.lay_effects.startAnimation(this.animSlideDown);
            this.lay_effects.setVisibility(View.GONE);
        } else if (this.lay_background.getVisibility() == View.VISIBLE) {
            this.lay_background.startAnimation(this.animSlideDown);
            this.img_oK.setVisibility(View.VISIBLE);
            this.btn_erase.setVisibility(View.VISIBLE);
            this.btnUndo.setVisibility(View.VISIBLE);
            this.btnRedo.setVisibility(View.VISIBLE);
            this.lay_background.setVisibility(View.GONE);
        } else if (lay_container.getVisibility() == View.VISIBLE) {
            lay_container.animate().translationX((float) (-lay_container.getRight())).setDuration(200).setInterpolator(new AccelerateInterpolator()).start();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    ThumbnailActivity.lay_container.setVisibility(View.GONE);
                    ThumbnailActivity.btn_layControls.setVisibility(View.VISIBLE);
                }
            }, 200);
        } else {
            showBackDialog();
        }
    }

    private void showBackDialog() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.leave_dialog);
        Button button = dialog.findViewById(R.id.btn_yes);
        Button button2 = dialog.findViewById(R.id.btn_no);
        ((TextView) dialog.findViewById(R.id.tv_loading)).setTypeface(setBoldFont());
        dialog.findViewById(R.id.rv_lay).setVisibility(View.GONE);
        ((TextView) dialog.findViewById(R.id.txterorr)).setTypeface(setBoldFont());
        ((TextView) dialog.findViewById(R.id.txt_free)).setTypeface(setBoldFont());
        button.setTypeface(setBoldFont());
        button2.setTypeface(setBoldFont());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.finish();
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void imageSavedSuccess() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.savesuccess_dialog);
        Button button = dialog.findViewById(R.id.btn_yes);
        Button button2 = dialog.findViewById(R.id.btn_no);
        ((ImageView) dialog.findViewById(R.id.img)).setImageURI(Uri.parse(this.filename));
        ((TextView) dialog.findViewById(R.id.txterorr)).setTypeface(setBoldFont());
        ((TextView) dialog.findViewById(R.id.txt_free)).setTypeface(setBoldFont());
        button.setTypeface(setBoldFont());
        button2.setTypeface(setBoldFont());
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.finish();
                dialog.dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ThumbnailActivity.this, ShareImageActivity.class);
                intent.putExtra("uri", ThumbnailActivity.this.filename);
                intent.putExtra("way", "Poster");
                ThumbnailActivity.this.startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showPicImageDialog() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_pic_dialog);
        ((TextView) dialog.findViewById(R.id.permission_des)).setTypeface(setNormalFont());
        ((TextView) dialog.findViewById(R.id.txtTitle)).setTypeface(setBoldFont());
        dialog.findViewById(R.id.iv_gallery).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.requestGalleryPermission();
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.iv_camera).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.requestCameraPermission();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void requestCameraPermission() {
        Dexter.withContext(this).withPermissions("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    ThumbnailActivity.this.onCameraButtonClick();
                }
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    ThumbnailActivity.this.showSettingsDialog();
                }
            }

            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            public void onError(DexterError dexterError) {
                Toast.makeText(ThumbnailActivity.this.getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
            }
        }).onSameThread().check();
    }

    public void ongetSticker() {
        this.color_Type = "colored";
        addSticker("", "", Constants.bitmap);
    }

    public void onColor(int i, String str, int i2) {
        if (i != 0) {
            int childCount = txtStkrRel.getChildCount();
            int i3 = 0;
            if (str.equals("txtShadow")) {
                while (i3 < childCount) {
                    View childAt = txtStkrRel.getChildAt(i3);
                    if (childAt instanceof AutofitTextRel) {
                        ((AutofitTextRel) txtStkrRel.getChildAt(i2)).setBorderVisibility(true);
                        AutofitTextRel autofitTextRel = (AutofitTextRel) childAt;
                        if (autofitTextRel.getBorderVisibility()) {
                            this.shadowColor = i;
                            autofitTextRel.setTextShadowColor(i);
                        }
                    }
                    i3++;
                }
            } else if (str.equals("txtBg")) {
                while (i3 < childCount) {
                    View childAt2 = txtStkrRel.getChildAt(i3);
                    if (childAt2 instanceof AutofitTextRel) {
                        ((AutofitTextRel) txtStkrRel.getChildAt(i2)).setBorderVisibility(true);
                        AutofitTextRel autofitTextRel2 = (AutofitTextRel) childAt2;
                        if (autofitTextRel2.getBorderVisibility()) {
                            this.bgColor = i;
                            this.bgDrawable = "0";
                            autofitTextRel2.setBgColor(i);
                            autofitTextRel2.setBgAlpha(this.seekBar3.getProgress());
                        }
                    }
                    i3++;
                }
            } else {
                View childAt3 = txtStkrRel.getChildAt(i2);
                if (childAt3 instanceof AutofitTextRel) {
                    ((AutofitTextRel) txtStkrRel.getChildAt(i2)).setBorderVisibility(true);
                    AutofitTextRel autofitTextRel3 = (AutofitTextRel) childAt3;
                    if (autofitTextRel3.getBorderVisibility()) {
                        this.tColor = i;
                        this.textColorSet = i;
                        autofitTextRel3.setTextColor(i);
                    }
                }
                if (childAt3 instanceof StickerView) {
                    ((StickerView) txtStkrRel.getChildAt(i2)).setBorderVisibility(true);
                    StickerView stickerView = (StickerView) childAt3;
                    if (stickerView.getBorderVisbilty()) {
                        this.stkrColorSet = i;
                        stickerView.setColor(i);
                    }
                }
            }
        } else {
            removeScroll();
            if (this.lay_TextMain.getVisibility() == View.VISIBLE) {
                this.lay_TextMain.startAnimation(this.animSlideDown);
                this.lay_TextMain.setVisibility(View.GONE);
            }
            if (this.lay_StkrMain.getVisibility() == View.VISIBLE) {
                this.lay_StkrMain.startAnimation(this.animSlideDown);
                this.lay_StkrMain.setVisibility(View.GONE);
            }
        }
    }


    public void errorDialogTempInfo() {
        final Dialog dialog = new Dialog(this, R.style.ThemeWithCorners);
        dialog.requestWindowFeature(1);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.error_dialog);
        ((TextView) dialog.findViewById(R.id.txterorr)).setTypeface(this.ttfHeader);
        ((TextView) dialog.findViewById(R.id.txt)).setTypeface(this.ttf);
        Button button = dialog.findViewById(R.id.btn_ok_e);
        button.setTypeface(this.ttf);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ThumbnailActivity.this.finish();
            }
        });
        Button button2 = dialog.findViewById(R.id.btn_conti);
        button2.setTypeface(this.ttf);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public Bitmap gaussinBlur(Activity activity2, Bitmap bitmap2) {
        try {
            GPUImage gPUImage = new GPUImage(activity2);
            GPUImageGaussianBlurFilter gPUImageGaussianBlurFilter = new GPUImageGaussianBlurFilter();
            gPUImage.setFilter(gPUImageGaussianBlurFilter);
            new FilterAdjuster(gPUImageGaussianBlurFilter).adjust(150);
            gPUImage.requestRender();
            return gPUImage.getBitmapWithFilterApplied(bitmap2);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getRemoveBoderPosition() {
        int childCount = txtStkrRel.getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = txtStkrRel.getChildAt(i2);
            if ((childAt instanceof AutofitTextRel) && ((AutofitTextRel) childAt).getBorderVisibility()) {
                i = i2;
            }
            if ((childAt instanceof StickerView) && ((StickerView) childAt).getBorderVisbilty()) {
                i = i2;
            }
        }
        return i;
    }


    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.broadcastReceiver);
        freeMemory();
    }

    public void freeMemory() {
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.bitmap = null;
        }
        Bitmap bitmap3 = imgBtmap;
        if (bitmap3 != null) {
            bitmap3.recycle();
            imgBtmap = null;
        }
        Bitmap bitmap4 = withoutWatermark;
        if (bitmap4 != null) {
            bitmap4.recycle();
            withoutWatermark = null;
        }
        Bitmap bitmap5 = btmSticker;
        if (bitmap5 != null) {
            bitmap5.recycle();
            btmSticker = null;
        }
        try {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Glide.get(ThumbnailActivity.this).clearDiskCache();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            Glide.get(this).clearMemory();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }


    public void onResume() {
        BroadcastReceiver broadcastReceiver2 = this.broadcastReceiver;
        registerReceiver(broadcastReceiver2, new IntentFilter(getPackageName() + ".USER_ACTION"));
        super.onResume();
    }

    private void fackClick() {
        this.lay_effects.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.lay_textEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seekbar_container.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seekbar_handle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seekLetterSpacing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seekLineSpacing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.verticalSeekBar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seekBar_shadow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seekShadowBlur.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seekOutlineSize.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seekBar3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seek_blur.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
        this.seek.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            }
        });
    }

    public void selectControl1() {
        this.txtTextControls.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
        this.txtFontsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Style.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_curve.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtColorsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtShadowControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtBgControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_outline_control.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
    }

    public void selectControl2() {
        this.txtTextControls.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtFontsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
        this.txt_fonts_Style.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Spacing.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_curve.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtColorsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtShadowControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtBgControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_outline_control.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
    }

    public void selectControl3() {
        this.txtTextControls.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtFontsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Style.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
        this.txt_fonts_Spacing.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_curve.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtColorsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtShadowControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtBgControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_outline_control.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
    }

    public void selectControl4() {
        this.txtTextControls.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtFontsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Style.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
        this.txt_fonts_Spacing.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_curve.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtColorsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtShadowControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtBgControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_outline_control.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
    }

    public void selectControl5() {
        this.txtTextControls.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtFontsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Style.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Spacing.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_curve.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
        this.txtColorsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtShadowControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtBgControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_outline_control.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
    }

    public void selectControl6() {
        this.txtTextControls.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtFontsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Style.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Spacing.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_curve.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtColorsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
        this.txtShadowControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtBgControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_outline_control.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
    }

    public void selectControl7() {
        this.txtTextControls.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtFontsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Style.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtColorsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_curve.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Spacing.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtShadowControl.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
        this.txtBgControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_outline_control.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
    }

    public void selectControl8() {
        this.txtTextControls.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtFontsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Style.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtColorsControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_curve.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txt_fonts_Spacing.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtShadowControl.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
        this.txtBgControl.setTextColor(getResources().getColor(R.color.tabtextcolor_selected));
        this.txt_outline_control.setTextColor(getResources().getColor(R.color.tabtextcolor_normal));
    }


    public void drawBackgroundImageFromDpTemp(String str, String str2, String str3, String str4) {
        this.lay_sticker.setVisibility(View.GONE);
        File file = new File(this.temp_path);
        if (file.exists()) {
            try {
                bitmapRatio(str, str3, ImageUtils.getResampleImageBitmap(Uri.parse(this.temp_path), this, (int) (this.screenWidth > this.screenHeight ? this.screenWidth : this.screenHeight)), str4);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (!str.equals("")) {
            new SavebackgrundAsync().execute(file.getName().replace(".png", ""), str, str3, str4);
        } else if (this.OneShow) {
            errorDialogTempInfo();
            this.OneShow = false;
        }
    }

    @Override
    public void selectedImage(int drawable) {
        setBitmapOverlay(drawable);

    }

    public class BlurOperationTwoAsync extends AsyncTask<String, Void, String> {
        ImageView background_blur;
        Bitmap btmp;
        Activity context;

        @Override
        public void onPreExecute() {
        }

        public BlurOperationTwoAsync(ThumbnailActivity thumbnailActivity, Bitmap bitmap, ImageView imageView) {
            this.context = thumbnailActivity;
            this.btmp = bitmap;
            this.background_blur = imageView;
        }


        public String doInBackground(String... strArr) {
            this.btmp = ThumbnailActivity.this.gaussinBlur(this.context, this.btmp);
            return "yes";
        }

        @Override
        public void onPostExecute(String str) {
            Bitmap bitmap = this.btmp;
            if (bitmap != null) {
                this.background_blur.setImageBitmap(bitmap);
            }
            ThumbnailActivity.txtStkrRel.removeAllViews();
            if (ThumbnailActivity.this.temp_path.equals("")) {
                LordStickersAsync lordStickersAsync = new LordStickersAsync();
                lordStickersAsync.execute("" + ThumbnailActivity.this.template_id);
                return;
            }
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), ".Thumbnail Design Stickers/category1");
            if (file.exists()) {
                try {
                    if (file.listFiles().length >= 7) {
                        LordStickersAsync lordStickersAsync2 = new LordStickersAsync();
                        lordStickersAsync2.execute("" + ThumbnailActivity.this.template_id);
                    } else if (new File(ThumbnailActivity.this.temp_path).exists()) {
                        LordStickersAsync lordStickersAsync3 = new LordStickersAsync();
                        lordStickersAsync3.execute("" + ThumbnailActivity.this.template_id);
                    } else {
                        LordStickersAsync lordStickersAsync4 = new LordStickersAsync();
                        lordStickersAsync4.execute("" + ThumbnailActivity.this.template_id);
                    }
                } catch (NullPointerException e) {
                    LordStickersAsync lordStickersAsync5 = new LordStickersAsync();
                    lordStickersAsync5.execute("" + ThumbnailActivity.this.template_id);
                    e.printStackTrace();
                }
            } else if (new File(ThumbnailActivity.this.temp_path).exists()) {
                LordStickersAsync lordStickersAsync6 = new LordStickersAsync();
                lordStickersAsync6.execute("" + ThumbnailActivity.this.template_id);
            } else {
                LordStickersAsync lordStickersAsync7 = new LordStickersAsync();
                lordStickersAsync7.execute("" + ThumbnailActivity.this.template_id);
            }
        }
    }

    private class LordStickersAsync extends AsyncTask<String, String, Boolean> {
        private LordStickersAsync() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }


        public Boolean doInBackground(String... strArr) {
            ArrayList<ElementInfo> arrayList;
            ArrayList<TextInfo> arrayList2;
            String str;
            DatabaseHandler dbHandler = DatabaseHandler.getDbHandler(ThumbnailActivity.this.getApplicationContext());
            if (ThumbnailActivity.this.myDesignFlag == 0) {
                arrayList2 = dbHandler.getTextInfoList(ThumbnailActivity.this.template_id);
                arrayList = dbHandler.getComponentInfoList(ThumbnailActivity.this.template_id, "STICKER");
            } else {
                arrayList2 = new ArrayList<>();
                arrayList = new ArrayList<>();

                for (int i = 0; i < ThumbnailActivity.this.stickerInfoArrayList.size(); i++) {
                    ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
                    int newWidht = thumbnailActivity.getNewWidht(Float.valueOf(thumbnailActivity.stickerInfoArrayList.get(i).getSt_x_pos()).floatValue(), Float.valueOf(ThumbnailActivity.this.stickerInfoArrayList.get(i).getSt_width()).floatValue());
                    ThumbnailActivity thumbnailActivity2 = ThumbnailActivity.this;
                    int newHeight = thumbnailActivity2.getNewHeight(Float.valueOf(thumbnailActivity2.stickerInfoArrayList.get(i).getSt_y_pos()).floatValue(), Float.valueOf(ThumbnailActivity.this.stickerInfoArrayList.get(i).getSt_height()).floatValue());
                    int i2 = newWidht < 10 ? 20 : (newWidht <= 10 || newWidht > 20) ? newWidht : 35;
                    int i3 = newHeight < 10 ? 20 : (newHeight <= 10 || newHeight > 20) ? newHeight : 35;
                    if (ThumbnailActivity.this.stickerInfoArrayList.get(i).getSt_field2() != null) {
                        str = ThumbnailActivity.this.stickerInfoArrayList.get(i).getSt_field2();
                    } else {
                        str = "";
                    }
                    float parseInt = (ThumbnailActivity.this.stickerInfoArrayList.get(i).getSt_rotation() == null || ThumbnailActivity.this.stickerInfoArrayList.get(i).getSt_rotation().equals("")) ? 0.0f : (float) Integer.parseInt(ThumbnailActivity.this.stickerInfoArrayList.get(i).getSt_rotation());
                    int i4 = ThumbnailActivity.this.post_id;
                    ThumbnailActivity thumbnailActivity3 = ThumbnailActivity.this;
                    float xpos = thumbnailActivity3.getXpos(Float.valueOf(thumbnailActivity3.stickerInfoArrayList.get(i).getSt_x_pos()).floatValue());
                    ThumbnailActivity thumbnailActivity4 = ThumbnailActivity.this;
                    arrayList.add(new ElementInfo(i4, xpos, thumbnailActivity4.getYpos(Float.valueOf(thumbnailActivity4.stickerInfoArrayList.get(i).getSt_y_pos()).floatValue()), i2, i3, parseInt, 0.0f, "", "STICKER", Integer.parseInt(ThumbnailActivity.this.stickerInfoArrayList.get(i).getSt_order()), 0, 255, 0, 0, 0, 0, ThumbnailActivity.this.stickerInfoArrayList.get(i).getSt_image(), "colored", 1, 0, str, "", "", null, null));
                }
                for (int i5 = 0; i5 < ThumbnailActivity.this.textInfoArrayList.size(); i5++) {
                    int i6 = ThumbnailActivity.this.post_id;
                    String text = ThumbnailActivity.this.textInfoArrayList.get(i5).getText();
                    String font_family = ThumbnailActivity.this.textInfoArrayList.get(i5).getFont_family();
                    int parseColor = Color.parseColor(ThumbnailActivity.this.textInfoArrayList.get(i5).getTxt_color());
                    ThumbnailActivity thumbnailActivity5 = ThumbnailActivity.this;
                    float xpos2 = thumbnailActivity5.getXpos(Float.valueOf(thumbnailActivity5.textInfoArrayList.get(i5).getTxt_x_pos()).floatValue());
                    ThumbnailActivity thumbnailActivity6 = ThumbnailActivity.this;
                    float ypos = thumbnailActivity6.getYpos(Float.valueOf(thumbnailActivity6.textInfoArrayList.get(i5).getTxt_y_pos()).floatValue());
                    ThumbnailActivity thumbnailActivity7 = ThumbnailActivity.this;
                    int newWidht2 = thumbnailActivity7.getNewWidht(Float.valueOf(thumbnailActivity7.textInfoArrayList.get(i5).getTxt_x_pos()).floatValue(), Float.valueOf(ThumbnailActivity.this.textInfoArrayList.get(i5).getTxt_width()).floatValue());
                    ThumbnailActivity thumbnailActivity8 = ThumbnailActivity.this;
                    arrayList2.add(new TextInfo(i6, text, font_family, parseColor, 100, ViewCompat.MEASURED_STATE_MASK, 0, "0", ViewCompat.MEASURED_STATE_MASK, 0, xpos2, ypos, newWidht2, thumbnailActivity8.getNewHeightText(Float.valueOf(thumbnailActivity8.textInfoArrayList.get(i5).getTxt_y_pos()).floatValue(), Float.valueOf(ThumbnailActivity.this.textInfoArrayList.get(i5).getTxt_height()).floatValue()), Float.parseFloat(ThumbnailActivity.this.textInfoArrayList.get(i5).getTxt_rotation()), "TEXT", Integer.parseInt(ThumbnailActivity.this.textInfoArrayList.get(i5).getTxt_order()), 0, 0, 0, 0, 0, "", "", "", 0.0f, 0.0f, 0, 0));
                }
            }
            dbHandler.close();
            ThumbnailActivity.this.txtShapeList = new HashMap<>();
            Iterator<TextInfo> it = arrayList2.iterator();
            while (it.hasNext()) {
                TextInfo next = it.next();
                ThumbnailActivity.this.txtShapeList.put(Integer.valueOf(next.getORDER()), next);
            }
            Iterator<ElementInfo> it2 = arrayList.iterator();
            while (it2.hasNext()) {
                ElementInfo next2 = it2.next();
                ThumbnailActivity.this.txtShapeList.put(Integer.valueOf(next2.getORDER()), next2);
            }
            return true;
        }

        @Override
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            ThumbnailActivity.this.dialogIs.dismiss();
            ArrayList arrayList = new ArrayList(ThumbnailActivity.this.txtShapeList.keySet());
            Collections.sort(arrayList);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                Object obj = ThumbnailActivity.this.txtShapeList.get(arrayList.get(i));
                if (obj instanceof ElementInfo) {
                    ElementInfo elementInfo = (ElementInfo) obj;
                    String stkr_path = elementInfo.getSTKR_PATH();
                    if (stkr_path.equals("")) {
                        StickerView stickerView = new StickerView(ThumbnailActivity.this);
                        ThumbnailActivity.txtStkrRel.addView(stickerView);
                        stickerView.optimizeScreen(ThumbnailActivity.this.screenWidth, ThumbnailActivity.this.screenHeight);
                        stickerView.setViewWH((float) ThumbnailActivity.this.main_rel.getWidth(), (float) ThumbnailActivity.this.main_rel.getHeight());
                        stickerView.setComponentInfo(elementInfo);
                        stickerView.setId(ViewIdGenerator.generateViewId());
                        stickerView.optimize(ThumbnailActivity.this.wr, ThumbnailActivity.this.hr);
                        stickerView.setOnTouchCallbackListener(ThumbnailActivity.this);
                        stickerView.setBorderVisibility(false);
                        ThumbnailActivity.this.sizeFull++;
                    } else {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), ".Thumbnail Design Stickers/category1");
                        if (!file.exists() && !file.mkdirs()) {
                            Log.d("", "Can't create directory to save image.");
                            ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
                            Toast.makeText(thumbnailActivity, thumbnailActivity.getResources().getString(R.string.create_dir_err), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), ".Thumbnail Design Stickers/category1").exists()) {
                            File file2 = new File(stkr_path);
                            if (file2.exists()) {
                                StickerView stickerView2 = new StickerView(ThumbnailActivity.this);
                                ThumbnailActivity.txtStkrRel.addView(stickerView2);
                                stickerView2.optimizeScreen(ThumbnailActivity.this.screenWidth, ThumbnailActivity.this.screenHeight);
                                stickerView2.setViewWH((float) ThumbnailActivity.this.main_rel.getWidth(), (float) ThumbnailActivity.this.main_rel.getHeight());
                                stickerView2.setComponentInfo(elementInfo);
                                stickerView2.setId(ViewIdGenerator.generateViewId());
                                stickerView2.optimize(ThumbnailActivity.this.wr, ThumbnailActivity.this.hr);
                                stickerView2.setOnTouchCallbackListener(ThumbnailActivity.this);
                                stickerView2.setBorderVisibility(false);
                                ThumbnailActivity.this.sizeFull++;
                            } else if (file2.getName().replace(".png", "").length() < 7) {
                                ThumbnailActivity thumbnailActivity2 = ThumbnailActivity.this;
                                thumbnailActivity2.dialogShow = false;
                                new SaveStickersAsync(obj).execute(stkr_path);
                            } else {
                                if (ThumbnailActivity.this.OneShow) {
                                    ThumbnailActivity thumbnailActivity3 = ThumbnailActivity.this;
                                    thumbnailActivity3.dialogShow = true;
                                    thumbnailActivity3.errorDialogTempInfo();
                                    ThumbnailActivity.this.OneShow = false;
                                }
                                ThumbnailActivity.this.sizeFull++;
                            }
                        } else {
                            File file3 = new File(stkr_path);
                            if (file3.exists()) {
                                StickerView stickerView3 = new StickerView(ThumbnailActivity.this);
                                ThumbnailActivity.txtStkrRel.addView(stickerView3);
                                stickerView3.optimizeScreen(ThumbnailActivity.this.screenWidth, ThumbnailActivity.this.screenHeight);
                                stickerView3.setViewWH((float) ThumbnailActivity.this.main_rel.getWidth(), (float) ThumbnailActivity.this.main_rel.getHeight());
                                stickerView3.setComponentInfo(elementInfo);
                                stickerView3.setId(ViewIdGenerator.generateViewId());
                                stickerView3.optimize(ThumbnailActivity.this.wr, ThumbnailActivity.this.hr);
                                stickerView3.setOnTouchCallbackListener(ThumbnailActivity.this);
                                stickerView3.setBorderVisibility(false);
                                ThumbnailActivity.this.sizeFull++;
                            } else if (file3.getName().replace(".png", "").length() < 7) {
                                ThumbnailActivity thumbnailActivity4 = ThumbnailActivity.this;
                                thumbnailActivity4.dialogShow = false;
                                new SaveStickersAsync(obj).execute(stkr_path);
                            } else {
                                if (ThumbnailActivity.this.OneShow) {
                                    ThumbnailActivity thumbnailActivity5 = ThumbnailActivity.this;
                                    thumbnailActivity5.dialogShow = true;
                                    thumbnailActivity5.errorDialogTempInfo();
                                    ThumbnailActivity.this.OneShow = false;
                                }
                                ThumbnailActivity.this.sizeFull++;
                            }
                        }
                    }
                } else {
                    AutofitTextRel autofitTextRel = new AutofitTextRel(ThumbnailActivity.this);
                    ThumbnailActivity.txtStkrRel.addView(autofitTextRel);
                    TextInfo textInfo = (TextInfo) obj;
                    autofitTextRel.setTextInfo(textInfo, false);
                    autofitTextRel.setId(ViewIdGenerator.generateViewId());
                    autofitTextRel.optimize(ThumbnailActivity.this.wr, ThumbnailActivity.this.hr);
                    autofitTextRel.setOnTouchCallbackListener(ThumbnailActivity.this);
                    autofitTextRel.setBorderVisibility(false);
                    ThumbnailActivity.this.fontName = textInfo.getFONT_NAME();
                    ThumbnailActivity.this.tColor = textInfo.getTEXT_COLOR();
                    ThumbnailActivity.this.shadowColor = textInfo.getSHADOW_COLOR();
                    ThumbnailActivity.this.shadowProg = textInfo.getSHADOW_PROG();
                    ThumbnailActivity.this.tAlpha = textInfo.getTEXT_ALPHA();
                    ThumbnailActivity.this.bgDrawable = textInfo.getBG_DRAWABLE();
                    ThumbnailActivity.this.bgAlpha = textInfo.getBG_ALPHA();
                    ThumbnailActivity.this.rotation = textInfo.getROTATION();
                    ThumbnailActivity.this.bgColor = textInfo.getBG_COLOR();
                    ThumbnailActivity.this.outerColor = textInfo.getOutLineColor();
                    ThumbnailActivity.this.outerSize = textInfo.getOutLineSize();
                    ThumbnailActivity.this.leftRightShadow = (int) textInfo.getLeftRighShadow();
                    ThumbnailActivity.this.topBottomShadow = (int) textInfo.getTopBottomShadow();
                    ThumbnailActivity.this.topBottomShadow = (int) textInfo.getTopBottomShadow();
                    ThumbnailActivity.this.sizeFull++;
                }
            }
            if (ThumbnailActivity.this.txtShapeList.size() == ThumbnailActivity.this.sizeFull && ThumbnailActivity.this.dialogShow) {
                try {
                    ThumbnailActivity.this.dialogIs.dismiss();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            if (!ThumbnailActivity.this.overlay_Name.equals("")) {
                ThumbnailActivity thumbnailActivity6 = ThumbnailActivity.this;
                thumbnailActivity6.setBitmapOverlay(getResources().getIdentifier(ThumbnailActivity.this.overlay_Name, "drawable", ThumbnailActivity.this.getPackageName()));
            }
            ThumbnailActivity.this.saveBitmapUndu();
        }
    }

    private class LordTemplateAsync extends AsyncTask<String, String, Boolean> {
        int indx;
        String postion;

        private LordTemplateAsync() {
            this.indx = 0;
            this.postion = "1";
        }

        @Override

        public void onPreExecute() {
            super.onPreExecute();
            ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
            thumbnailActivity.dialogIs = new ProgressDialog(thumbnailActivity);
            ThumbnailActivity.this.dialogIs.setMessage(ThumbnailActivity.this.getResources().getString(R.string.plzwait));
            ThumbnailActivity.this.dialogIs.setCancelable(false);
            ThumbnailActivity.this.dialogIs.show();
        }


        public Boolean doInBackground(String... strArr) {
            if (ThumbnailActivity.this.myDesignFlag == 0) {
                this.indx = Integer.parseInt(strArr[0]);
            } else {
                this.indx = 0;
            }
            TemplateInfo templateInfo = ThumbnailActivity.this.templateList.get(this.indx);
            ThumbnailActivity.this.template_id = templateInfo.getTEMPLATE_ID();
            ThumbnailActivity.this.frame_Name = templateInfo.getFRAME_NAME();
            ThumbnailActivity.this.temp_path = templateInfo.getTEMP_PATH();
            ThumbnailActivity.this.ratio = templateInfo.getRATIO();
            Log.e("Ratio", "==" + ThumbnailActivity.this.ratio);
            ThumbnailActivity.this.profile = templateInfo.getPROFILE_TYPE();
            String seek_value = templateInfo.getSEEK_VALUE();
            ThumbnailActivity.this.hex = templateInfo.getTEMPCOLOR();
            ThumbnailActivity.this.overlay_Name = templateInfo.getOVERLAY_NAME();
            ThumbnailActivity.this.overlay_opacty = templateInfo.getOVERLAY_OPACITY();
            ThumbnailActivity.this.overlay_blur = templateInfo.getOVERLAY_BLUR();
            ThumbnailActivity.this.seekValue = Integer.parseInt(seek_value);
            return true;
        }

        @Override
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            String valueOf = String.valueOf(Integer.parseInt(this.postion));
            if (!ThumbnailActivity.this.templateList.get(this.indx).getTYPE().equals("USER")) {
                return;
            }
            if (ThumbnailActivity.this.myDesignFlag != 0) {
                ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
                thumbnailActivity.drawBackgroundImage(thumbnailActivity.ratio, valueOf, ThumbnailActivity.this.frame_Name, "created");
                return;
            }
            ThumbnailActivity thumbnailActivity2 = ThumbnailActivity.this;
            thumbnailActivity2.drawBackgroundImageFromDpTemp(thumbnailActivity2.ratio, valueOf, ThumbnailActivity.this.temp_path, "created");
        }
    }

    private class SaveStickersAsync extends AsyncTask<String, String, Boolean> {
        Object objk;
        String stkr_path;

        public SaveStickersAsync(Object obj) {
            this.objk = obj;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }


        public Boolean doInBackground(String... strArr) {
            String str = strArr[0];
            this.stkr_path = ((ElementInfo) this.objk).getSTKR_PATH();
            try {
                Bitmap decodeResource = BitmapFactory.decodeResource(ThumbnailActivity.this.getResources(), ThumbnailActivity.this.getResources().getIdentifier(str, "drawable", ThumbnailActivity.this.getPackageName()));
                if (decodeResource != null) {
                    return Boolean.valueOf(Constants.saveBitmapObject(ThumbnailActivity.this, decodeResource, this.stkr_path));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }


        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            ThumbnailActivity.this.sizeFull++;
            if (ThumbnailActivity.this.txtShapeList.size() == ThumbnailActivity.this.sizeFull) {
                ThumbnailActivity.this.dialogShow = true;
            }
            if (bool.booleanValue()) {
                StickerView stickerView = new StickerView(ThumbnailActivity.this);
                ThumbnailActivity.txtStkrRel.addView(stickerView);
                stickerView.optimizeScreen(ThumbnailActivity.this.screenWidth, ThumbnailActivity.this.screenHeight);
                stickerView.setViewWH((float) ThumbnailActivity.this.main_rel.getWidth(), (float) ThumbnailActivity.this.main_rel.getHeight());
                stickerView.setComponentInfo((ElementInfo) this.objk);
                stickerView.setId(ViewIdGenerator.generateViewId());
                stickerView.optimize(ThumbnailActivity.this.wr, ThumbnailActivity.this.hr);
                stickerView.setOnTouchCallbackListener(ThumbnailActivity.this);
                stickerView.setBorderVisibility(false);
            }
            if (ThumbnailActivity.this.dialogShow) {
                ThumbnailActivity.this.dialogIs.dismiss();
            }
        }
    }

    private class SavebackgrundAsync extends AsyncTask<String, String, Boolean> {
        private String crted;
        private String profile;
        private String ratio;

        private SavebackgrundAsync() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }


        public Boolean doInBackground(String... strArr) {
            String str = strArr[0];
            this.ratio = strArr[1];
            this.profile = strArr[2];
            this.crted = strArr[3];
            try {
                Bitmap decodeResource = BitmapFactory.decodeResource(ThumbnailActivity.this.getResources(), ThumbnailActivity.this.getResources().getIdentifier(str, "drawable", ThumbnailActivity.this.getPackageName()));
                if (decodeResource != null) {
                    return Boolean.valueOf(Constants.saveBitmapObject(ThumbnailActivity.this, decodeResource, ThumbnailActivity.this.temp_path));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (bool.booleanValue()) {
                try {
                    ThumbnailActivity.this.bitmapRatio(this.ratio, this.profile, ImageUtils.getResampleImageBitmap(Uri.parse(ThumbnailActivity.this.temp_path), ThumbnailActivity.this, (int) (ThumbnailActivity.this.screenWidth > ThumbnailActivity.this.screenHeight ? ThumbnailActivity.this.screenWidth : ThumbnailActivity.this.screenHeight)), this.crted);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ThumbnailActivity.txtStkrRel.removeAllViews();
            }
        }
    }


    public void saveBitmapUndu() {
        try {
            this.tempID++;
            TemplateInfo templateInfo = new TemplateInfo();
            templateInfo.setTHUMB_URI("");
            templateInfo.setTEMPLATE_ID(this.tempID);
            templateInfo.setFRAME_NAME(this.frame_Name);
            templateInfo.setRATIO(this.ratio);
            templateInfo.setPROFILE_TYPE(this.profile);
            templateInfo.setSEEK_VALUE(String.valueOf(this.seekValue));
            templateInfo.setTYPE("USER");
            templateInfo.setTEMP_PATH(this.temp_path);
            templateInfo.setTEMPCOLOR(this.hex);
            templateInfo.setOVERLAY_NAME(this.overlay_Name);
            templateInfo.setOVERLAY_OPACITY(this.seek.getProgress());
            templateInfo.setOVERLAY_BLUR(this.seek_blur.getProgress());
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            int childCount = txtStkrRel.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = txtStkrRel.getChildAt(i);
                if (childAt instanceof AutofitTextRel) {
                    TextInfo textInfo = ((AutofitTextRel) childAt).getTextInfo();
                    textInfo.setTEMPLATE_ID(this.template_id);
                    textInfo.setORDER(i);
                    textInfo.setTYPE("TEXT");
                    arrayList.add(textInfo);
                } else {
                    ElementInfo componentInfo = ((StickerView) txtStkrRel.getChildAt(i)).getComponentInfo();
                    componentInfo.setTEMPLATE_ID(this.template_id);
                    componentInfo.setTYPE("STICKER");
                    componentInfo.setORDER(i);
                    arrayList2.add(componentInfo);
                }
            }
            templateInfo.setTextInfoArrayList(arrayList);
            templateInfo.setElementInfoArrayList(arrayList2);
            this.templateListU_R.add(templateInfo);
            iconVisibility();
        } catch (Exception e) {
            Log.i("testing", "Exception " + e.getMessage());
            e.printStackTrace();
        } catch (Throwable th) {
        }
    }

    public void loadSaveUnduRedo(TemplateInfo templateInfo) {
        this.template_id = templateInfo.getTEMPLATE_ID();
        this.frame_Name = templateInfo.getFRAME_NAME();
        this.temp_path = templateInfo.getTEMP_PATH();
        this.ratio = templateInfo.getRATIO();
        this.profile = templateInfo.getPROFILE_TYPE();
        this.tempID = templateInfo.getTEMPLATE_ID();
        String seek_value = templateInfo.getSEEK_VALUE();
        this.hex = templateInfo.getTEMPCOLOR();
        this.overlay_Name = templateInfo.getOVERLAY_NAME();
        this.overlay_opacty = templateInfo.getOVERLAY_OPACITY();
        this.overlay_blur = templateInfo.getOVERLAY_BLUR();
        this.seekValue = Integer.parseInt(seek_value);
        this.textInfosU_R = templateInfo.getTextInfoArrayList();
        this.elementInfosU_R = templateInfo.getElementInfoArrayList();
        this.progressBarUndo.setVisibility(View.VISIBLE);
        this.btnRedo.setVisibility(View.GONE);
        this.btnUndo.setVisibility(View.GONE);
        LordStickersAsyncU_R lordStickersAsyncU_R = new LordStickersAsyncU_R();
        lordStickersAsyncU_R.execute("" + this.tempID);
    }

    private class LordStickersAsyncU_R extends AsyncTask<String, String, Boolean> {
        private LordStickersAsyncU_R() {
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }


        public Boolean doInBackground(String... strArr) {
            ThumbnailActivity.this.txtShapeList = new HashMap<>();
            Iterator<TextInfo> it = ThumbnailActivity.this.textInfosU_R.iterator();
            while (it.hasNext()) {
                TextInfo next = it.next();
                ThumbnailActivity.this.txtShapeList.put(Integer.valueOf(next.getORDER()), next);
            }
            Iterator<ElementInfo> it2 = ThumbnailActivity.this.elementInfosU_R.iterator();
            while (it2.hasNext()) {
                ElementInfo next2 = it2.next();
                ThumbnailActivity.this.txtShapeList.put(Integer.valueOf(next2.getORDER()), next2);
            }
            return true;
        }

        @Override
        public void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            ThumbnailActivity.txtStkrRel.removeAllViews();
            ArrayList arrayList = new ArrayList(ThumbnailActivity.this.txtShapeList.keySet());
            Collections.sort(arrayList);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                Object obj = ThumbnailActivity.this.txtShapeList.get(arrayList.get(i));
                if (obj instanceof ElementInfo) {
                    ElementInfo elementInfo = (ElementInfo) obj;
                    String stkr_path = elementInfo.getSTKR_PATH();
                    if (stkr_path.equals("")) {
                        StickerView stickerView = new StickerView(ThumbnailActivity.this, true);
                        ThumbnailActivity.txtStkrRel.addView(stickerView);
                        stickerView.optimizeScreen(ThumbnailActivity.this.screenWidth, ThumbnailActivity.this.screenHeight);
                        stickerView.setViewWH((float) ThumbnailActivity.this.main_rel.getWidth(), (float) ThumbnailActivity.this.main_rel.getHeight());
                        stickerView.setComponentInfo(elementInfo);
                        stickerView.setId(ViewIdGenerator.generateViewId());
                        stickerView.optimize(ThumbnailActivity.this.wr, ThumbnailActivity.this.hr);
                        stickerView.setOnTouchCallbackListener(ThumbnailActivity.this);
                        stickerView.setBorderVisibility(false);
                        ThumbnailActivity.this.sizeFull++;
                    } else {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), ".Invitation Stickers/category1");
                        if (!file.exists() && !file.mkdirs()) {
                            Log.d("", "Can't create directory to save image.");
                            ThumbnailActivity thumbnailActivity = ThumbnailActivity.this;
                            Toast.makeText(thumbnailActivity, thumbnailActivity.getResources().getString(R.string.create_dir_err), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), ".Invitation Stickers/category1").exists()) {
                            File file2 = new File(stkr_path);
                            if (file2.exists()) {
                                StickerView stickerView2 = new StickerView(ThumbnailActivity.this, true);
                                ThumbnailActivity.txtStkrRel.addView(stickerView2);
                                stickerView2.optimizeScreen(ThumbnailActivity.this.screenWidth, ThumbnailActivity.this.screenHeight);
                                stickerView2.setViewWH((float) ThumbnailActivity.this.main_rel.getWidth(), (float) ThumbnailActivity.this.main_rel.getHeight());
                                stickerView2.setComponentInfo(elementInfo);
                                stickerView2.setId(ViewIdGenerator.generateViewId());
                                stickerView2.optimize(ThumbnailActivity.this.wr, ThumbnailActivity.this.hr);
                                stickerView2.setOnTouchCallbackListener(ThumbnailActivity.this);
                                stickerView2.setBorderVisibility(false);
                                ThumbnailActivity.this.sizeFull++;
                            } else if (file2.getName().replace(".png", "").length() < 7) {
                                ThumbnailActivity.this.dialogShow = false;
                            } else {
                                if (ThumbnailActivity.this.OneShow) {
                                    ThumbnailActivity thumbnailActivity2 = ThumbnailActivity.this;
                                    thumbnailActivity2.dialogShow = true;
                                    thumbnailActivity2.errorDialogTempInfo();
                                    ThumbnailActivity.this.OneShow = false;
                                }
                                ThumbnailActivity.this.sizeFull++;
                            }
                        } else {
                            File file3 = new File(stkr_path);
                            if (file3.exists()) {
                                StickerView stickerView3 = new StickerView(ThumbnailActivity.this, true);
                                ThumbnailActivity.txtStkrRel.addView(stickerView3);
                                stickerView3.optimizeScreen(ThumbnailActivity.this.screenWidth, ThumbnailActivity.this.screenHeight);
                                stickerView3.setViewWH((float) ThumbnailActivity.this.main_rel.getWidth(), (float) ThumbnailActivity.this.main_rel.getHeight());
                                stickerView3.setComponentInfo(elementInfo);
                                stickerView3.setId(ViewIdGenerator.generateViewId());
                                stickerView3.optimize(ThumbnailActivity.this.wr, ThumbnailActivity.this.hr);
                                stickerView3.setOnTouchCallbackListener(ThumbnailActivity.this);
                                stickerView3.setBorderVisibility(false);
                                ThumbnailActivity.this.sizeFull++;
                            } else if (file3.getName().replace(".png", "").length() < 7) {
                                ThumbnailActivity.this.dialogShow = false;
                            } else {
                                if (ThumbnailActivity.this.OneShow) {
                                    ThumbnailActivity thumbnailActivity3 = ThumbnailActivity.this;
                                    thumbnailActivity3.dialogShow = true;
                                    thumbnailActivity3.errorDialogTempInfo();
                                    ThumbnailActivity.this.OneShow = false;
                                }
                                ThumbnailActivity.this.sizeFull++;
                            }
                        }
                    }
                } else {
                    AutofitTextRel autofitTextRel = new AutofitTextRel(ThumbnailActivity.this, true);
                    ThumbnailActivity.txtStkrRel.addView(autofitTextRel);
                    TextInfo textInfo = (TextInfo) obj;
                    autofitTextRel.setTextInfo(textInfo, false);
                    autofitTextRel.setId(ViewIdGenerator.generateViewId());
                    autofitTextRel.optimize(ThumbnailActivity.this.wr, ThumbnailActivity.this.hr);
                    autofitTextRel.setOnTouchCallbackListener(ThumbnailActivity.this);
                    autofitTextRel.setBorderVisibility(false);
                    ThumbnailActivity.this.fontName = textInfo.getFONT_NAME();
                    ThumbnailActivity.this.tColor = textInfo.getTEXT_COLOR();
                    ThumbnailActivity.this.shadowColor = textInfo.getSHADOW_COLOR();
                    ThumbnailActivity.this.shadowProg = textInfo.getSHADOW_PROG();
                    ThumbnailActivity.this.tAlpha = textInfo.getTEXT_ALPHA();
                    ThumbnailActivity.this.bgDrawable = textInfo.getBG_DRAWABLE();
                    ThumbnailActivity.this.bgAlpha = textInfo.getBG_ALPHA();
                    ThumbnailActivity.this.rotation = textInfo.getROTATION();
                    ThumbnailActivity.this.bgColor = textInfo.getBG_COLOR();
                    ThumbnailActivity.this.sizeFull++;
                }
            }
            ThumbnailActivity.this.progressBarUndo.setVisibility(View.GONE);
            ThumbnailActivity.this.btnRedo.setVisibility(View.VISIBLE);
            ThumbnailActivity.this.btnUndo.setVisibility(View.VISIBLE);
        }
    }

    public void undo() {
        if (this.lay_TextMain.getVisibility() == View.VISIBLE) {
            this.lay_TextMain.setVisibility(View.GONE);
        }
        if (this.lay_StkrMain.getVisibility() == View.VISIBLE) {
            this.lay_StkrMain.setVisibility(View.GONE);
        }
        if (this.templateListU_R.size() > 2) {
            this.btnUndo.setImageResource(R.drawable.undo);
        } else {
            this.btnUndo.setImageResource(R.drawable.undo_disable);
        }
        if (this.templateListU_R.size() > 1) {
            ArrayList<TemplateInfo> arrayList = this.templateListU_R;
            loadSaveUnduRedo(arrayList.get(arrayList.size() - 2));
            ArrayList<TemplateInfo> arrayList2 = this.templateListR_U;
            ArrayList<TemplateInfo> arrayList3 = this.templateListU_R;
            arrayList2.add(arrayList3.get(arrayList3.size() - 1));
            ArrayList<TemplateInfo> arrayList4 = this.templateListU_R;
            arrayList4.remove(arrayList4.get(arrayList4.size() - 1));
        }
        iconVisibility();
    }

    public void redo() {
        if (this.lay_TextMain.getVisibility() == View.VISIBLE) {
            this.lay_TextMain.setVisibility(View.GONE);
        }
        if (this.lay_StkrMain.getVisibility() == View.VISIBLE) {
            this.lay_StkrMain.setVisibility(View.GONE);
        }
        if (this.templateListR_U.size() > 1) {
            this.btnRedo.setImageResource(R.drawable.redo);
        } else {
            this.btnRedo.setImageResource(R.drawable.redo_disable);
        }
        if (this.templateListR_U.size() > 0) {
            ArrayList<TemplateInfo> arrayList = this.templateListR_U;
            loadSaveUnduRedo(arrayList.get(arrayList.size() - 1));
            ArrayList<TemplateInfo> arrayList2 = this.templateListU_R;
            ArrayList<TemplateInfo> arrayList3 = this.templateListR_U;
            arrayList2.add(arrayList3.get(arrayList3.size() - 1));
            ArrayList<TemplateInfo> arrayList4 = this.templateListR_U;
            arrayList4.remove(arrayList4.get(arrayList4.size() - 1));
        }
        iconVisibility();
    }

    public void iconVisibility() {
        if (this.templateListU_R.size() > 1) {
            this.btnUndo.setImageResource(R.drawable.undo);
        } else {
            this.btnUndo.setImageResource(R.drawable.undo_disable);
        }
        if (this.templateListR_U.size() > 0) {
            this.btnRedo.setImageResource(R.drawable.redo);
        } else {
            this.btnRedo.setImageResource(R.drawable.redo_disable);
        }
    }
}
