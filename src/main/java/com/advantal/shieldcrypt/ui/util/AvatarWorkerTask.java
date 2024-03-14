package com.advantal.shieldcrypt.ui.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.annotation.DimenRes;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.services.AvatarService;
import com.advantal.shieldcrypt.ui.XmppActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.lang.ref.WeakReference;
import java.util.concurrent.RejectedExecutionException;

public class AvatarWorkerTask extends AsyncTask<AvatarService.Avatarable, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    private @DimenRes
    final int size;
    private AvatarService.Avatarable avatarable = null;

    public AvatarWorkerTask(ImageView imageView, @DimenRes int size) {
        imageViewReference = new WeakReference<>(imageView);
        this.size = size;
    }

    public static boolean cancelPotentialWork(AvatarService.Avatarable avatarable, ImageView imageView) {
        final AvatarWorkerTask workerTask = getBitmapWorkerTask(imageView);

        if (workerTask != null) {
            final AvatarService.Avatarable old = workerTask.avatarable;
            if (old == null || avatarable != old) {
                workerTask.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    public static AvatarWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getAvatarWorkerTask();
            }
        }
        return null;
    }

    public static void loadAvatar(final AvatarService.Avatarable avatarable, final ImageView imageView, final @DimenRes int size) {
        if (cancelPotentialWork(avatarable, imageView)) {
            final XmppActivity activity = XmppActivity.find(imageView);
            if (activity == null) {
                return;
            }
            final Bitmap bm = activity.avatarService().get(avatarable, (int) activity.getResources().getDimension(size), true);
            setContentDescription(avatarable, imageView);


//            MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getUserProfilePick(avatarable.getAvatarName());

            if (bm != null) {
                cancelPotentialWork(avatarable, imageView);
                imageView.setImageBitmap(bm);
                imageView.setBackgroundColor(0x00000000);
            } else {
                imageView.setBackgroundColor(avatarable.getAvatarBackgroundColor());
                imageView.setImageDrawable(null);
                final AvatarWorkerTask task = new AvatarWorkerTask(imageView, size);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(activity.getResources(), null, task);
                imageView.setImageDrawable(asyncDrawable);
                try {
                    task.execute(avatarable);
                } catch (final RejectedExecutionException ignored) {
                }
            }
        }
    }

    public static void loadAvatar(final AvatarService.Avatarable avatarable, final ImageView imageView, final @DimenRes int size, String nm) {
      /*  String tnm = "";
        if (nm == null || nm.equals("")) {
            tnm = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getUserNameByPhone(avatarable.getAvatarName());
            if (tnm != null && !tnm.equals("")) {
                nm = tnm.substring(0, 1);
            } else {
                tnm = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).groupDao().getUserNameByPhone(avatarable.getAvatarName());
                if (tnm != null && !tnm.equals("")) {
                    nm = tnm.substring(0, 1);
                }
            }
        }*/
        if (cancelPotentialWork(avatarable, imageView)) {
            final XmppActivity activity = XmppActivity.find(imageView);
            if (activity == null) {
                return;
            }
            final Bitmap bm = activity.avatarService().get(avatarable, (int) activity.getResources().getDimension(size), true);
//            setContentDescription2(avatarable, imageView, nm);
            setContentDescription(avatarable, imageView);


            if (nm != null && !nm.equals("")) {
                cancelPotentialWork(avatarable, imageView);
                Glide.with(activity).load(nm).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
                imageView.setBackgroundColor(0x00000000);
            } else {
                imageView.setBackgroundColor(avatarable.getAvatarBackgroundColor());
                imageView.setImageDrawable(null);
                final AvatarWorkerTask task = new AvatarWorkerTask(imageView, size);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(activity.getResources(), null, task);
                imageView.setImageDrawable(asyncDrawable);
                try {
                    task.execute(avatarable);//a
                } catch (final RejectedExecutionException ignored) {
                }
            }
        }
    }

    private static void setContentDescription(final AvatarService.Avatarable avatarable, final ImageView imageView) {
        final Context context = imageView.getContext();
        if (avatarable instanceof Account) {
            imageView.setContentDescription(context.getString(R.string.your_avatar));
        } else {
//            imageView.setContentDescription(context.getString(R.string.avatar_for_x, avatarable.getAvatarName()));
            imageView.setContentDescription(context.getString(R.string.avatar_for_x,"A"));
//            imageView.setContentDescription(context.getString(R.string.avatar_for_x, MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getUserNameByPhone(avatarable.getAvatarName()).substring(0, 1)));
        }
    }

    @Override
    protected Bitmap doInBackground(AvatarService.Avatarable... params) {
        this.avatarable = params[0];
        final XmppActivity activity = XmppActivity.find(imageViewReference);
        if (activity == null) {
            return null;
        }
        return activity.avatarService().get(avatarable, (int) activity.getResources().getDimension(size), isCancelled());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null && !isCancelled()) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
                imageView.setBackgroundColor(0x00000000);
            }
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<AvatarWorkerTask> avatarWorkerTaskReference;

        AsyncDrawable(Resources res, Bitmap bitmap, AvatarWorkerTask workerTask) {
            super(res, bitmap);
            avatarWorkerTaskReference = new WeakReference<>(workerTask);
        }

        AvatarWorkerTask getAvatarWorkerTask() {
            return avatarWorkerTaskReference.get();
        }
    }
}
