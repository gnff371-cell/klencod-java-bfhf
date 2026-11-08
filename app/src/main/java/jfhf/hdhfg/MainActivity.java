
package jfhf.hdhfg;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(8, 13, 24);
    private final int CARD = Color.rgb(21, 31, 48);
    private final int RED = Color.rgb(229, 9, 53);
    private final int WHITE = Color.WHITE;
    private final int GRAY = Color.rgb(155, 168, 188);

    // Football-data.org API
    private final String API_TOKEN =
            "6e5e41fe1fd14518a3b3bb21ed6929b2";

    private LinearLayout root;
    private LinearLayout matchesList;

    private String matchMode = "today";

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        showSplash();
    }

    private int dp(float value) {
        return (int) (value *
                getResources().getDisplayMetrics().density);
    }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private TextView text(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(Typeface.create(
                "sans-serif", Typeface.BOLD));
        return t;
    }

    // SPLASH SCREEN

    private void showSplash() {

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER);
        screen.setPadding(dp(30), dp(30), dp(30), dp(30));
        screen.setBackgroundColor(BG);

        TextView logo = text("SAREE3", 36, WHITE);
        screen.addView(logo);

        TextView live = text("LIVE", 14, RED);
        live.setLetterSpacing(0.35f);
        screen.addView(live);

        TextView slogan = text("SPORTS EXPERIENCE", 11, GRAY);
        slogan.setPadding(0, dp(12), 0, dp(55));
        screen.addView(slogan);

        TextView loading = text("جاري تجهيز التطبيق", 13, GRAY);
        screen.addView(loading);

        TextView percentage = text("0%", 14, WHITE);
        percentage.setPadding(0, dp(15), 0, dp(10));
        screen.addView(percentage);

        android.widget.ProgressBar progress =
                new android.widget.ProgressBar(
                        this, null,
                        android.R.attr.progressBarStyleHorizontal
                );

        progress.setMax(100);
        progress.setProgress(0);
        progress.setProgressTintList(
                android.content.res.ColorStateList.valueOf(RED)
        );
        progress.setProgressBackgroundTintList(
                android.content.res.ColorStateList.valueOf(CARD)
        );

        screen.addView(progress,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(7)));

        setContentView(screen);

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(4000);

        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            progress.setProgress(value);
            percentage.setText(value + "%");
        });

        animator.addListener(
                new android.animation.Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(
                    android.animation.Animator a) {}

            @Override
            public void onAnimationEnd(
                    android.animation.Animator a) {
                showHome();
            }

            @Override
            public void onAnimationCancel(
                    android.animation.Animator a) {}

            @Override
            public void onAnimationRepeat(
                    android.animation.Animator a) {}
        });

        animator.start();
    }

    // ROOT

    private void prepareRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(
                dp(18), dp(12), dp(18), dp(8));
    }

    private TextView makeButton(String title, int size) {
        TextView button = text(title, size, WHITE);
        button.setBackground(shape(CARD, 18));
        button.setPadding(
                dp(10), dp(18), dp(10), dp(18));
        return button;
    }

    // HOME

    private void showHome() {

        prepareRoot();

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView logo = text("SAREE3", 25, WHITE);
        logo.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);

        header.addView(logo,
                new LinearLayout.LayoutParams(0, dp(55), 1));

        TextView live = text("LIVE", 12, RED);
        live.setPadding(
                dp(12), dp(7), dp(12), dp(7));
        live.setBackground(shape(CARD, 20));

        header.addView(live);
        root.addView(header);

        ScrollView scroll = new ScrollView(this);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView subtitle = text(
                "SPORTS EXPERIENCE", 10, GRAY);

        subtitle.setGravity(
                Gravity.LEFT | Gravity.CENTER_VERTICAL);
        subtitle.setPadding(0, dp(5), 0, dp(18));

        content.addView(subtitle);

        TextView hero = text(
                "عالم الرياضة\nبين يديك", 25, WHITE);

        hero.setGravity(
                Gravity.LEFT | Gravity.CENTER_VERTICAL);

        hero.setPadding(
                dp(20), dp(25), dp(20), dp(25));

        GradientDrawable heroBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{
                        Color.rgb(43, 13, 30),
                        Color.rgb(17, 27, 44)
                }
        );

        heroBg.setCornerRadius(dp(20));
        hero.setBackground(heroBg);

        content.addView(hero);

        TextView title = text(
                "استكشف القنوات", 19, WHITE);

        title.setGravity(
                Gravity.LEFT | Gravity.CENTER_VERTICAL);

        title.setPadding(0, dp(25), 0, dp(15));

        content.addView(title);

        addCategoryGrid(content);

        TextView footer = text(
                "SAREE3 LIVE  •  PREMIUM", 10, GRAY);

        footer.setPadding(0, dp(25), 0, dp(20));

        content.addView(footer);

        scroll.addView(content);

        root.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        addBottomBar(root);

        setContentView(root);
    }

    // CHANNELS

    private void addCategoryGrid(LinearLayout content) {

        String[] names = {
                "beIN SPORTS",
                "SSC SPORTS",
                "القنوات المصرية",
                "القنوات العراقية",
                "قنوات رياضية أخرى",
                "قنوات متنوعة"
        };

        String[] symbols = {
                "beIN", "SSC", "ON",
                "IRAQ", "SPORT", "TV"
        };

        for (int i = 0; i < names.length; i += 2) {

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = i; j < i + 2 && j < names.length; j++) {

                final int index = j;

                LinearLayout card = new LinearLayout(this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setGravity(Gravity.CENTER);
                card.setBackground(shape(CARD, 18));

                card.setPadding(
                        dp(8), dp(20), dp(8), dp(20));

                TextView symbol = text(
                        symbols[j], 25, WHITE);

                card.addView(symbol);

                TextView name = text(
                        names[j], 12, GRAY);

                name.setPadding(0, dp(10), 0, 0);

                card.addView(name);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                0, dp(125), 1);

                params.setMargins(
                        dp(5), dp(5), dp(5), dp(5));

                row.addView(card, params);

                card.setOnClickListener(v -> {
                    if (index == 0) {
                        showBeinChannels();
                    } else {
                        showCategory(names[index]);
                    }
                });
            }

            content.addView(row);
        }
    }

    private void showBeinChannels() {
        showChannelPage("beIN SPORTS", "beIN SPORTS ", 6);
    }

    private void showCategory(String category) {
        showChannelPage(category, category + " ", 6);
    }

    private void showChannelPage(
            String title, String prefix, int count) {

        prepareRoot();

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text("‹", 35, WHITE);

        header.addView(back,
                new LinearLayout.LayoutParams(dp(45), dp(55)));

        TextView heading = text(title, 19, WHITE);
        heading.setGravity(
                Gravity.CENTER_VERTICAL | Gravity.LEFT);

        header.addView(heading);

        back.setOnClickListener(v -> showHome());

        root.addView(header);

        TextView description = text(
                "اختر القناة التي تريد مشاهدتها", 12, GRAY);

        description.setGravity(Gravity.LEFT);
        description.setPadding(0, dp(12), 0, dp(20));

        root.addView(description);

        ScrollView scroll = new ScrollView(this);

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);

        for (int i = 1; i <= count; i += 2) {

            LinearLayout row = new LinearLayout(this);

            for (int j = i; j <= i + 1 && j <= count; j++) {

                TextView channel = makeButton(
                        prefix + j, 15);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                0, dp(110), 1);

                params.setMargins(
                        dp(5), dp(5), dp(5), dp(5));

                row.addView(channel, params);

                channel.setOnClickListener(v -> {
                    Toast.makeText(
                            this,
                            "أضف رابط البث المرخص لتشغيل القناة",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }

            list.addView(row);
        }

        scroll.addView(list);

        root.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
    }

    // MATCHES PAGE

    private void showMatchesPage() {

        prepareRoot();

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text("‹", 35, WHITE);

        header.addView(back,
                new LinearLayout.LayoutParams(dp(45), dp(55)));

        back.setOnClickListener(v -> showHome());

        TextView heading = text(
                "جداول المباريات", 20, WHITE);

        heading.setGravity(
                Gravity.CENTER_VERTICAL | Gravity.LEFT);

        header.addView(heading);
        root.addView(header);

        TextView subtitle = text(
                "مواعيد ونتائج كرة القدم", 12, GRAY);

        subtitle.setGravity(Gravity.LEFT);
        subtitle.setPadding(0, dp(5), 0, dp(15));

        root.addView(subtitle);

        LinearLayout tabs = new LinearLayout(this);
        tabs.setOrientation(LinearLayout.HORIZONTAL);

        String[] labels = {
                "اليوم", "القادمة", "المنتهية"
        };

        String[] modes = {
                "today", "upcoming", "finished"
        };

        for (int i = 0; i < labels.length; i++) {

            final String mode = modes[i];

            TextView tab = text(labels[i], 12, WHITE);
            tab.setBackground(shape(CARD, 14));

            tab.setPadding(
                    dp(12), dp(13), dp(12), dp(13));

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(
                            0, dp(45), 1);

            p.setMargins(
                    dp(3), 0, dp(3), dp(10));

            tabs.addView(tab, p);

            tab.setOnClickListener(v ->
                    fetchMatches(mode));
        }

        root.addView(tabs);

        TextView refresh = text(
                "⟳  تحديث المباريات", 13, WHITE);

        refresh.setBackground(shape(RED, 13));

        refresh.setPadding(
                dp(12), dp(12), dp(12), dp(12));

        root.addView(refresh);

        refresh.setOnClickListener(v ->
                fetchMatches(matchMode));

        ScrollView scroll = new ScrollView(this);

        matchesList = new LinearLayout(this);
        matchesList.setOrientation(LinearLayout.VERTICAL);

        matchesList.setPadding(
                0, dp(12), 0, dp(15));

        scroll.addView(matchesList);

        root.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        addBottomBar(root);

        setContentView(root);

        fetchMatches("today");
    }

    // DATE

    private String getDate(int days) {

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, days);

        return new SimpleDateFormat(
                "yyyy-MM-dd", Locale.US
        ).format(calendar.getTime());
    }

    // API CONNECTION

    private void fetchMatches(String mode) {

        matchMode = mode;

        if (matchesList == null) return;

        matchesList.removeAllViews();

        addMatchMessage("جاري تحميل المباريات...");

        new Thread(() -> {

            HttpURLConnection connection = null;

            try {

                String from;
                String to;

                if (mode.equals("today")) {

                    from = getDate(0);
                    to = getDate(0);

                } else if (mode.equals("upcoming")) {

                    from = getDate(0);
                    to = getDate(7);

                } else {

                    from = getDate(-7);
                    to = getDate(0);
                }

                String address =
                        "https://api.football-data.org/v4/matches"
                        + "?dateFrom=" + from
                        + "&dateTo=" + to;

                URL url = new URL(address);

                connection =
                        (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");

                connection.setRequestProperty(
                        "X-Auth-Token", API_TOKEN);

                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                int response = connection.getResponseCode();

                BufferedReader reader;

                if (response >= 200 && response < 300) {

                    reader = new BufferedReader(
                            new InputStreamReader(
                                    connection.getInputStream()));

                } else {

                    reader = new BufferedReader(
                            new InputStreamReader(
                                    connection.getErrorStream()));
                }

                StringBuilder result = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();

                if (response != 200) {

                    String error = result.toString();

                    runOnUiThread(() -> {

                        matchesList.removeAllViews();

                        addMatchMessage(
                                "فشل الاتصال بالخدمة\n"
                                + "رمز الخطأ: " + response
                                + "\n" + error
                        );
                    });

                    return;
                }

                JSONObject json =
                        new JSONObject(result.toString());

                JSONArray matches =
                        json.optJSONArray("matches");

                runOnUiThread(() ->
                        displayMatches(matches, mode));

            } catch (Exception e) {

                runOnUiThread(() -> {

                    matchesList.removeAllViews();

                    addMatchMessage(
                            "تعذر الاتصال بالإنترنت\n"
                            + e.getMessage()
                    );
                });

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }

        }).start();
    }

    // DISPLAY MATCHES

    private void displayMatches(
            JSONArray matches, String mode) {

        matchesList.removeAllViews();

        if (matches == null || matches.length() == 0) {

            addMatchMessage(
                    "لا توجد مباريات في هذه الفترة");

            return;
        }

        int displayed = 0;

        for (int i = 0; i < matches.length(); i++) {

            JSONObject match = matches.optJSONObject(i);

            if (match == null) continue;

            String status = match.optString("status", "");

            if (mode.equals("upcoming")
                    && !status.equals("SCHEDULED")
                    && !status.equals("TIMED")) {
                continue;
            }

            if (mode.equals("finished")
                    && !status.equals("FINISHED")) {
                continue;
            }

            JSONObject home = match.optJSONObject("homeTeam");
            JSONObject away = match.optJSONObject("awayTeam");

            JSONObject score = match.optJSONObject("score");

            JSONObject fullTime = score == null ? null :
                    score.optJSONObject("fullTime");

            JSONObject competition =
                    match.optJSONObject("competition");

            String homeName = home == null
                    ? "الفريق الأول"
                    : home.optString("shortName",
                    home.optString("name", "الفريق الأول"));

            String awayName = away == null
                    ? "الفريق الثاني"
                    : away.optString("shortName",
                    away.optString("name", "الفريق الثاني"));

            String homeScore = fullTime == null
                    || fullTime.isNull("home")
                    ? "-" : fullTime.optString("home");

            String awayScore = fullTime == null
                    || fullTime.isNull("away")
                    ? "-" : fullTime.optString("away");

            String time = formatMatchTime(
                    match.optString("utcDate", ""));

            String league = competition == null
                    ? "كرة القدم"
                    : competition.optString(
                    "name", "كرة القدم");

            String statusText;

            switch (status) {

                case "FINISHED":
                    statusText = "انتهت المباراة";
                    break;

                case "IN_PLAY":
                    statusText = "مباشر";
                    break;

                case "PAUSED":
                    statusText = "استراحة";
                    break;

                case "SCHEDULED":
                case "TIMED":
                    statusText = "لم تبدأ";
                    break;

                default:
                    statusText = status;
                    break;
            }

            LinearLayout card = new LinearLayout(this);

            card.setOrientation(LinearLayout.VERTICAL);

            card.setPadding(
                    dp(14), dp(13), dp(14), dp(13));

            card.setBackground(shape(CARD, 17));

            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(-1, -2);

            cardParams.setMargins(
                    0, dp(5), 0, dp(7));

            TextView leagueText = text(
                    league, 11, GRAY);

            leagueText.setGravity(Gravity.RIGHT);

            card.addView(leagueText);

            int statusColor = status.equals("IN_PLAY")
                    ? RED : GRAY;

            TextView matchTime = text(
                    time + "  •  " + statusText,
                    11, statusColor);

            matchTime.setPadding(
                    0, dp(7), 0, dp(10));

            card.addView(matchTime);

            LinearLayout teams = new LinearLayout(this);

            teams.setGravity(Gravity.CENTER);
            teams.setOrientation(LinearLayout.HORIZONTAL);

            TextView homeView = text(
                    homeName, 13, WHITE);

            homeView.setMaxLines(2);

            teams.addView(homeView,
                    new LinearLayout.LayoutParams(
                            0, -2, 1));

            TextView resultView = text(
                    homeScore + "  -  " + awayScore,
                    21, WHITE);

            resultView.setPadding(
                    dp(5), 0, dp(5), 0);

            teams.addView(resultView);

            TextView awayView = text(
                    awayName, 13, WHITE);

            awayView.setMaxLines(2);

            teams.addView(awayView,
                    new LinearLayout.LayoutParams(
                            0, -2, 1));

            card.addView(teams);

            matchesList.addView(card, cardParams);

            displayed++;
        }

        if (displayed == 0) {
            addMatchMessage(
                    "لا توجد مباريات مطابقة لهذا التصنيف");
        }
    }

    // MATCH TIME

    private String formatMatchTime(String utc) {

        try {

            SimpleDateFormat input =
                    new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss'Z'",
                            Locale.US);

            input.setTimeZone(
                    TimeZone.getTimeZone("UTC"));

            Date date = input.parse(utc);

            SimpleDateFormat output =
                    new SimpleDateFormat(
                            "dd/MM  HH:mm",
                            Locale.getDefault());

            output.setTimeZone(TimeZone.getDefault());

            return output.format(date);

        } catch (Exception e) {

            return "--:--";
        }
    }

    // MESSAGE

    private void addMatchMessage(String message) {

        TextView view = text(message, 13, GRAY);

        view.setPadding(
                dp(10), dp(25), dp(10), dp(25));

        matchesList.addView(view);
    }

    // BOTTOM NAVIGATION

    private void addBottomBar(LinearLayout parent) {

        LinearLayout bar = new LinearLayout(this);

        bar.setGravity(Gravity.CENTER);
        bar.setBackground(shape(CARD, 18));

        bar.setPadding(
                0, dp(12), 0, dp(12));

        String[] items = {
                "الرئيسية",
                "القنوات",
                "المباريات",
                "المفضلة",
                "الإعدادات"
        };

        for (String item : items) {

            TextView t = text(item, 10, GRAY);

            bar.addView(t,
                    new LinearLayout.LayoutParams(
                            0, dp(35), 1));

            t.setOnClickListener(v -> {

                if (item.equals("الرئيسية")
                        || item.equals("القنوات")) {

                    showHome();

                } else if (item.equals("المباريات")) {

                    showMatchesPage();

                } else {

                    Toast.makeText(
                            this, item,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        parent.addView(bar);
    }

    @Override
    public void onBackPressed() {
        showHome();
    }
}
