package com.example.sensors;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HalfCircleProgressBar extends View {

    private Paint paint;
    private int progress = 0;  // Valeur de progression entre 0 et 100
    private int maxProgress = 100;

    public HalfCircleProgressBar(Context context) {
        super(context);
        init();
    }

    public HalfCircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HalfCircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
    }

    // Méthode pour mettre à jour la progression
    public void setProgress(int progress) {
        if (progress >= 0 && progress <= maxProgress) {
            this.progress = progress;
            invalidate();  // Re-dessiner la vue
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int radius = Math.min(width, height) / 2 - 20;  // Rayon du demi-cercle (en tenant compte du padding)
        float sweepAngle = (progress / (float) maxProgress) * 180;  // Calcul de l'angle de progression (sur 180°)

        // Dessiner l'arrière du demi-cercle (fond)
        paint.setColor(Color.LTGRAY);
        canvas.drawArc(20, 20, width - 20, height - 20, 180, 180, false, paint);

        // Dessiner la barre de progression
        paint.setColor(Color.parseColor("#FFA726"));
        canvas.drawArc(20, 20, width - 20, height - 20, 180, sweepAngle, false, paint);
    }
}
