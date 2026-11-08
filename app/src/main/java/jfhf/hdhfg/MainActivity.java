
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
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final int BG = Color.rgb(8, 13, 24);
    private final int CARD = Color.rgb(21, 31, 48);
    private final int RED = Color.rgb(229, 9, 53);
    private final int WHITE = Color.WHITE;
    private final int GRAY = Color.rgb(155, 168, 188);

    private LinearLayout root;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        showSplash();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density);
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
        t.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        return t;
    }

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
                        ViewGroup.LayoutParams.MATCH_PARENT, dp(7)));

        setContentView(screen);

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(4000);

        animator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            progress.setProgress(value);
            percentage.setText(value + "%");
        });

        animator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator a) {}

            @Override
            public void onAnimationEnd(android.animation.Animator a) {
                showHome();
            }

            @Override
            public void onAnimationCancel(android.animation.Animator a) {}

            @Override
            public void onAnimationRepeat(android.animation.Animator a) {}
        });

        animator.start();
    }

    private void prepareRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(18), dp(12), dp(18), dp(8));
    }

    private TextView makeButton(String title, int size) {
        TextView button = text(title, size, WHITE);
        button.setBackground(shape(CARD, 18));
        button.setPadding(dp(10), dp(18), dp(10), dp(18));
        return button;
    }

    private void showHome() {
        prepareRoot();

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);

        TextView logo = text("SAREE3", 25, WHITE);
        logo.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        header.addView(logo, new LinearLayout.LayoutParams(0, dp(55), 1));

        TextView live = text("LIVE", 12, RED);
        live.setPadding(dp(12), dp(7), dp(12), dp(7));
        live.setBackground(shape(CARD, 20));
        header.addView(live);

        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView subtitle = text("SPORTS EXPERIENCE", 10, GRAY);
        subtitle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        subtitle.setPadding(0, dp(5), 0, dp(18));
        content.addView(subtitle);

        TextView hero = text("عالم الرياضة\nبين يديك", 25, WHITE);
        hero.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        hero.setPadding(dp(20), dp(25), dp(20), dp(25));

        GradientDrawable heroBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.rgb(43, 13, 30), Color.rgb(17, 27, 44)}
        );
        heroBg.setCornerRadius(dp(20));
        hero.setBackground(heroBg);

        content.addView(hero);

        TextView title = text("استكشف القنوات", 19, WHITE);
        title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        title.setPadding(0, dp(25), 0, dp(15));
        content.addView(title);

        addCategoryGrid(content);

        TextView footer = text("SAREE3 LIVE  •  PREMIUM", 10, GRAY);
        footer.setPadding(0, dp(25), 0, dp(20));
        content.addView(footer);

        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1));

        addBottomBar(root);

        setContentView(root);
    }

    private void addCategoryGrid(LinearLayout content) {
        String[] names = {
                "beIN SPORTS", "SSC SPORTS",
                "القنوات المصرية", "القنوات العراقية",
                "قنوات رياضية أخرى", "قنوات متنوعة"
        };

        String[] symbols = {
                "beIN", "SSC", "ON", "IRAQ", "SPORT", "TV"
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
                card.setPadding(dp(8), dp(20), dp(8), dp(20));

                TextView symbol = text(symbols[j], 25, WHITE);
                card.addView(symbol);

                TextView name = text(names[j], 12, GRAY);
                name.setPadding(0, dp(10), 0, 0);
                card.addView(name);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(0, dp(125), 1);
                params.setMargins(dp(5), dp(5), dp(5), dp(5));

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

    private void showChannelPage(String title, String prefix, int count) {
        prepareRoot();

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text("‹", 35, WHITE);
        header.addView(back, new LinearLayout.LayoutParams(dp(45), dp(55)));

        TextView heading = text(title, 19, WHITE);
        heading.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        header.addView(heading);

        back.setOnClickListener(v -> showHome());
        root.addView(header);

        TextView description = text("اختر القناة التي تريد مشاهدتها", 12, GRAY);
        description.setGravity(Gravity.LEFT);
        description.setPadding(0, dp(12), 0, dp(20));
        root.addView(description);

        ScrollView scroll = new ScrollView(this);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);

        for (int i = 1; i <= count; i += 2) {
            LinearLayout row = new LinearLayout(this);

            for (int j = i; j <= i + 1 && j <= count; j++) {
                final int channelNumber = j;

                TextView channel = makeButton(
                        prefix + channelNumber, 15);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(0, dp(110), 1);
                params.setMargins(dp(5), dp(5), dp(5), dp(5));

                row.addView(channel, params);

                channel.setOnClickListener(v -> {
                    Toast.makeText(
                            this,
                            "سيتم تشغيل القناة بعد إضافة رابط البث المرخص",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }

            list.addView(row);
        }

        scroll.addView(list);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
    }

    private void addBottomBar(LinearLayout parent) {
        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(Gravity.CENTER);
        bar.setBackground(shape(CARD, 18));
        bar.setPadding(0, dp(12), 0, dp(12));

        String[] items = {"الرئيسية", "القنوات", "المفضلة", "الإعدادات"};

        for (String item : items) {
            TextView t = text(item, 11, GRAY);
            bar.addView(t, new LinearLayout.LayoutParams(0, dp(35), 1));

            t.setOnClickListener(v -> {
                if (item.equals("الرئيسية") || item.equals("القنوات")) {
                    showHome();
                } else {
                    Toast.makeText(this, item, Toast.LENGTH_SHORT).show();
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