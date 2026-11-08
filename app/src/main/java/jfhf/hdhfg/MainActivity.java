package jfhf.hdhfg;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {

    private static final String API_URL = "https://api.football-data.org/v4/matches";
    // ضع مفتاح API الخاص بك هنا إذا كان لديك مفتاح خاص (اختياري للطلبات المحدودة)
    private static final String API_KEY = ""; 
    private static final String STREAM_URL = "http://mag.tiger-ott.net/live/KEBD2U/7F8W2T/242255.m3u8";

    private FrameLayout rootLayout;
    private LinearLayout matchesContainer;
    private ProgressBar progressBar;
    private FrameLayout playerContainer;
    private PlayerView playerView;
    private ExoPlayer player;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();
        fetchMatches();
    }

    private void initUI() {
        rootLayout = new FrameLayout(this);
        rootLayout.setBackgroundColor(Color.parseColor("#121212"));

        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // شريط العنوان العلوي
        TextView header = new TextView(this);
        header.setText("مباريات اليوم & البث المباشر");
        header.setTextColor(Color.WHITE);
        header.setTextSize(20);
        header.setTypeface(null, Typeface.BOLD);
        header.setGravity(Gravity.CENTER);
        header.setPadding(24, 40, 24, 20);
        contentLayout.addView(header);

        // زر تشغيل beIN SPORTS 1
        Button liveBtn = new Button(this);
        liveBtn.setText("📺 بث مباشر: beIN SPORTS 1");
        liveBtn.setTextColor(Color.WHITE);
        liveBtn.setTextSize(16);
        liveBtn.setBackgroundColor(Color.parseColor("#E50914"));
        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.setMargins(32, 10, 32, 20);
        liveBtn.setLayoutParams(btnParams);
        liveBtn.setOnClickListener(v -> startLiveStream());
        contentLayout.addView(liveBtn);

        // قائمة المباريات داخل ScrollView
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

        matchesContainer = new LinearLayout(this);
        matchesContainer.setOrientation(LinearLayout.VERTICAL);
        matchesContainer.setPadding(24, 10, 24, 30);
        scrollView.addView(matchesContainer);

        contentLayout.addView(scrollView);
        rootLayout.addView(contentLayout);

        // مؤشر التحميل
        progressBar = new ProgressBar(this);
        FrameLayout.LayoutParams pbParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pbParams.gravity = Gravity.CENTER;
        rootLayout.addView(progressBar, pbParams);

        // حاوية مشغل البث (مخفية في البداية)
        playerContainer = new FrameLayout(this);
        playerContainer.setBackgroundColor(Color.BLACK);
        playerContainer.setVisibility(View.GONE);
        playerContainer.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        playerView = new PlayerView(this);
        playerView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        playerContainer.addView(playerView);

        // زر إغلاق البث والعودة للمباريات
        Button closePlayerBtn = new Button(this);
        closePlayerBtn.setText("✕ إغلاق البث");
        closePlayerBtn.setTextColor(Color.WHITE);
        closePlayerBtn.setBackgroundColor(Color.parseColor("#80000000"));
        FrameLayout.LayoutParams closeParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeParams.gravity = Gravity.TOP | Gravity.START;
        closeParams.setMargins(30, 40, 0, 0);
        closePlayerBtn.setLayoutParams(closeParams);
        closePlayerBtn.setOnClickListener(v -> stopLiveStream());
        playerContainer.addView(closePlayerBtn);

        rootLayout.addView(playerContainer);

        setContentView(rootLayout);
    }

    private void fetchMatches() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(API_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                if (!API_KEY.isEmpty()) {
                    conn.setRequestProperty("X-Auth-Token", API_KEY);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    JSONObject data = new JSONObject(response.toString());
                    JSONArray matches = data.optJSONArray("matches");

                    mainHandler.post(() -> displayMatches(matches));
                } else {
                    mainHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "تعذر جلب البيانات: " + responseCode, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "حدث خطأ بالاتصال: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    private void displayMatches(JSONArray matches) {
        progressBar.setVisibility(View.GONE);
        matchesContainer.removeAllViews();

        if (matches == null || matches.length() == 0) {
            TextView emptyText = new TextView(this);
            emptyText.setText("لا توجد مباريات معروضة حالياً");
            emptyText.setTextColor(Color.GRAY);
            emptyText.setTextSize(16);
            emptyText.setGravity(Gravity.CENTER);
            matchesContainer.addView(emptyText);
            return;
        }

        for (int i = 0; i < matches.length(); i++) {
            JSONObject match = matches.optJSONObject(i);
            if (match == null) continue;

            JSONObject homeTeam = match.optJSONObject("homeTeam");
            JSONObject awayTeam = match.optJSONObject("awayTeam");
            JSONObject score = match.optJSONObject("score");
            JSONObject fullTime = (score != null) ? score.optJSONObject("fullTime") : null;

            String homeName = (homeTeam != null) ? homeTeam.optString("name", "Home") : "Home";
            String awayName = (awayTeam != null) ? awayTeam.optString("name", "Away") : "Away";
            String homeCrest = (homeTeam != null) ? homeTeam.optString("crest", "") : "";
            String awayCrest = (awayTeam != null) ? awayTeam.optString("crest", "") : "";

            String status = match.optString("status", "");
            String homeScore = (fullTime != null && !fullTime.isNull("home")) ? String.valueOf(fullTime.optInt("home")) : "-";
            String awayScore = (fullTime != null && !fullTime.isNull("away")) ? String.valueOf(fullTime.optInt("away")) : "-";

            matchesContainer.addView(createMatchCard(homeName, homeCrest, awayName, awayCrest, homeScore, awayScore, status));
        }
    }

    private View createMatchCard(String homeName, String homeCrest, String awayName, String awayCrest,
                                 String homeScore, String awayScore, String status) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(Color.parseColor("#1F1F1F"));
        card.setPadding(20, 20, 20, 20);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 20);
        card.setLayoutParams(cardParams);

        // صف عرض حالة المباراة
        TextView statusText = new TextView(this);
        statusText.setText(status);
        statusText.setTextColor(Color.parseColor("#00E676"));
        statusText.setTextSize(12);
        statusText.setGravity(Gravity.CENTER);
        card.addView(statusText);

        // صف الفريقين والنتيجة والشعارات
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // الفريق المستضيف
        LinearLayout homeCol = createTeamColumn(homeName, homeCrest);
        row.addView(homeCol);

        // النتيجة
        TextView scoreText = new TextView(this);
        scoreText.setText(homeScore + "  :  " + awayScore);
        scoreText.setTextColor(Color.WHITE);
        scoreText.setTextSize(18);
        scoreText.setTypeface(null, Typeface.BOLD);
        scoreText.setGravity(Gravity.CENTER);
        scoreText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
        row.addView(scoreText);

        // الفريق الضيف
        LinearLayout awayCol = createTeamColumn(awayName, awayCrest);
        row.addView(awayCol);

        card.addView(row);
        return card;
    }

    private LinearLayout createTeamColumn(String name, String crestUrl) {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER_HORIZONTAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f));

        ImageView logoView = new ImageView(this);
        int size = dpToPx(44);
        logoView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
        logoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        col.addView(logoView);

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextColor(Color.WHITE);
        nameView.setTextSize(13);
        nameView.setGravity(Gravity.CENTER_HORIZONTAL);
        nameView.setPadding(6, 8, 6, 0);
        col.addView(nameView);

        // تحميل الصورة في الخلفية بالـ Java الخالص
        loadCrestImage(crestUrl, logoView);

        return col;
    }

    private void loadCrestImage(String urlString, ImageView targetView) {
        if (urlString == null || urlString.isEmpty()) {
            return;
        }

        executor.execute(() -> {
            Bitmap bmp = null;
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception ignored) {
            } finally {
                if (conn != null) conn.disconnect();
            }

            final Bitmap result = bmp;
            if (result != null) {
                mainHandler.post(() -> targetView.setImageBitmap(result));
            }
        });
    }

    private void startLiveStream() {
        playerContainer.setVisibility(View.VISIBLE);

        if (player == null) {
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
        }

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(STREAM_URL));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void stopLiveStream() {
        if (player != null) {
            player.stop();
            player.clearMediaItems();
        }
        playerContainer.setVisibility(View.GONE);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
        executor.shutdown();
    }

    @Override
    public void onBackPressed() {
        if (playerContainer.getVisibility() == View.VISIBLE) {
            stopLiveStream();
        } else {
            super.onBackPressed();
        }
    }
}
