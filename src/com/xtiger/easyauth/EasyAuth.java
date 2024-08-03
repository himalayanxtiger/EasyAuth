package com.xtiger.easyauth;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.AndroidViewComponent;
import com.google.appinventor.components.runtime.Form;
import com.google.appinventor.components.runtime.ReplForm;

import java.io.File;
import java.io.InputStream;

public class EasyAuth extends AndroidNonvisibleComponent {

    private Context context;
    private Form form;
    private FrameLayout cardView;
    private ImageView logoImageView;
    private EditText signInUsernameEditText, signInPasswordEditText;
    private EditText signUpEmailEditText, signUpUsernameEditText, signUpPasswordEditText;
    private Button signInButton, signUpButton;
    private TextView forgotPasswordTextView, createAccountTextView, alreadyHaveAccountTextView;
    private LinearLayout signInLayout, signUpLayout;
    private Typeface typeface;

    public EasyAuth(ComponentContainer container) {
        super(container.$form());
        context = container.$context();
        form = container.$form();
    }

    @SimpleFunction(description = "Creates and shows the auth UI")
    public void ShowAuthUI(AndroidViewComponent arrangement, String logoPath, String fontPath) {
        if (!(arrangement.getView() instanceof ViewGroup)) {
            return;
        }
        ViewGroup parentView = (ViewGroup) arrangement.getView();

        loadTypeface(fontPath);

        cardView = new FrameLayout(context);
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(32, 32, 32, 32);
        cardView.setLayoutParams(cardParams);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(Color.WHITE);
        shape.setCornerRadius(16);
        shape.setStroke(2, Color.LTGRAY);
        cardView.setBackground(shape);
        cardView.setElevation(8);

        LinearLayout contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        contentLayout.setPadding(32, 32, 32, 32);

        logoImageView = new ImageView(context);
        new LoadLogoTask(logoImageView).execute(logoPath);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(200, 200);
        logoParams.gravity = android.view.Gravity.CENTER;
        logoParams.setMargins(0, 0, 0, 32);
        logoImageView.setLayoutParams(logoParams);

        signInUsernameEditText = createEditText("Username");
        signInPasswordEditText = createEditText("Password", true);

        signUpEmailEditText = createEditText("Email");
        signUpUsernameEditText = createEditText("Username");
        signUpPasswordEditText = createEditText("Password", true);

        signInButton = createButton("Sign In", Color.parseColor("#2196F3"));
        signUpButton = createButton("Sign Up", Color.parseColor("#4CAF50"));

        forgotPasswordTextView = createTextView("Forgot Password?", Color.parseColor("#2196F3"));
        createAccountTextView = createTextView("Don't have an account? Create Now", Color.parseColor("#2196F3"));
        alreadyHaveAccountTextView = createTextView("Already have an account? Sign In", Color.parseColor("#2196F3"));

        signInLayout = new LinearLayout(context);
        signInLayout.setOrientation(LinearLayout.VERTICAL);
        signInLayout.addView(signInUsernameEditText);
        signInLayout.addView(signInPasswordEditText);
        signInLayout.addView(signInButton);
        signInLayout.addView(forgotPasswordTextView);
        signInLayout.addView(createAccountTextView);

        signUpLayout = new LinearLayout(context);
        signUpLayout.setOrientation(LinearLayout.VERTICAL);
        signUpLayout.setVisibility(View.GONE);
        signUpLayout.addView(signUpEmailEditText);
        signUpLayout.addView(signUpUsernameEditText);
        signUpLayout.addView(signUpPasswordEditText);
        signUpLayout.addView(signUpButton);
        signUpLayout.addView(alreadyHaveAccountTextView);

        contentLayout.addView(logoImageView);
        contentLayout.addView(signInLayout);
        contentLayout.addView(signUpLayout);

        cardView.addView(contentLayout);
        parentView.addView(cardView);

        setClickListeners();
        applyCustomFont();
        applyEntryAnimation(cardView);
    }

    private EditText createEditText(String hint, boolean isPassword) {
        EditText editText = new EditText(context);
        editText.setHint(hint);
        if (isPassword) {
            editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 16, 0, 16);
        editText.setLayoutParams(params);
        return editText;
    }

    private EditText createEditText(String hint) {
        return createEditText(hint, false);
    }

    private Button createButton(String text, int color) {
        Button button = new Button(context);
        button.setText(text);
        button.setBackgroundColor(color);
        button.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 32, 0, 16);
        button.setLayoutParams(params);
        return button;
    }

    private TextView createTextView(String text, int color) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(color);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = android.view.Gravity.CENTER;
        params.setMargins(0, 16, 0, 0);
        textView.setLayoutParams(params);
        return textView;
    }

    private void setClickListeners() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = signInUsernameEditText.getText().toString();
                String password = signInPasswordEditText.getText().toString();
                SignInAttempted(username, password);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signUpEmailEditText.getText().toString();
                String username = signUpUsernameEditText.getText().toString();
                String password = signUpPasswordEditText.getText().toString();
                SignUpAttempted(email, username, password);
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordClicked();
            }
        });

        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSignUpUI();
            }
        });

        alreadyHaveAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSignUpUI();
            }
        });
    }

    private void toggleSignUpUI() {
        if (signInLayout.getVisibility() == View.VISIBLE) {
            signInLayout.setVisibility(View.GONE);
            signUpLayout.setVisibility(View.VISIBLE);
            applyEntryAnimation(signUpLayout);
        } else {
            signUpLayout.setVisibility(View.GONE);
            signInLayout.setVisibility(View.VISIBLE);
            applyEntryAnimation(signInLayout);
        }
    }

    private void loadTypeface(String typefacePath) {
        try {
            if (isCompanion()) {
                final String packageName = form.getPackageName();
                final String platform = packageName.contains("makeroid")
                        ? "Makeroid"
                        : packageName.contains("Niotron")
                        ? "Niotron"
                        : packageName.contains("Appzard")
                        ? "Appzard"
                        : "AppInventor";

                typefacePath = android.os.Build.VERSION.SDK_INT > 28
                        ? "/storage/emulated/0/Android/data/" + packageName + "/files/assets/" + typefacePath
                        : "/storage/emulated/0/" + platform + "/assets/" + typefacePath;
                typeface = Typeface.createFromFile(new File(typefacePath));
            } else {
                typeface = Typeface.createFromAsset(form.getAssets(), typefacePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            typeface = Typeface.DEFAULT;
        }
    }

    private boolean isCompanion() {
        return form instanceof ReplForm;
    }

    private void applyCustomFont() {
        signInUsernameEditText.setTypeface(typeface);
        signInPasswordEditText.setTypeface(typeface);
        signUpEmailEditText.setTypeface(typeface);
        signUpUsernameEditText.setTypeface(typeface);
        signUpPasswordEditText.setTypeface(typeface);
        signInButton.setTypeface(typeface);
        signUpButton.setTypeface(typeface);
        forgotPasswordTextView.setTypeface(typeface);
        createAccountTextView.setTypeface(typeface);
        alreadyHaveAccountTextView.setTypeface(typeface);
    }

    private void applyEntryAnimation(View view) {
        view.setAlpha(0f);
        view.setTranslationY(50);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 50, 0);

        animatorSet.playTogether(alpha, translationY);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    @SimpleFunction(description = "Toogle UIs between SignIn & SignUp")
    public void ToogleUI() {
        toggleSignUpUI();
    }

    @SimpleEvent(description = "Triggered when sign-in button is clicked")
    public void SignInAttempted(String username, String password) {
        EventDispatcher.dispatchEvent(this, "SignInAttempted", username, password);
    }

    @SimpleEvent(description = "Triggered when sign-up button is clicked")
    public void SignUpAttempted(String email, String username, String password) {
        EventDispatcher.dispatchEvent(this, "SignUpAttempted", email, username, password);
    }

    @SimpleEvent(description = "Triggered when forgot password is clicked")
    public void ForgotPasswordClicked() {
        EventDispatcher.dispatchEvent(this, "ForgotPasswordClicked");
    }

    @SimpleProperty(description = "Set the sign-in button color")
    public void SignInButtonColor(int color) {
        if (signInButton != null) {
            signInButton.setBackgroundColor(color);
        }
    }

    @SimpleProperty(description = "Set the sign-up button color")
    public void SignUpButtonColor(int color) {
        if (signUpButton != null) {
            signUpButton.setBackgroundColor(color);
        }
    }

    @SimpleProperty(description = "Set the forgot password text color")
    public void ForgotPasswordTextColor(int color) {
        if (forgotPasswordTextView != null) {
            forgotPasswordTextView.setTextColor(color);
        }
    }

    private class LoadLogoTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadLogoTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... paths) {
            String path = paths[0];
            Bitmap bitmap = null;
            try {
                if (isCompanion()) {
                    final String packageName = form.getPackageName();
                    final String platform = packageName.contains("makeroid")
                            ? "Makeroid"
                            : packageName.contains("Niotron")
                            ? "Niotron"
                            : packageName.contains("Appzard")
                            ? "Appzard"
                            : "AppInventor";

                    path = android.os.Build.VERSION.SDK_INT > 28
                            ? "/storage/emulated/0/Android/data/" + packageName + "/files/assets/" + path
                            : "/storage/emulated/0/" + platform + "/assets/" + path;
                    bitmap = BitmapFactory.decodeFile(path);
                } else {
                    InputStream inputStream = form.getAssets().open(path);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                bmImage.setImageBitmap(result);
            }
        }
    }
}