package com.advantal.shieldcrypt.ui.adapter;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.databinding.ConversationListRowBinding;
import com.advantal.shieldcrypt.entities.Contact;
import com.advantal.shieldcrypt.entities.Conversation;
import com.advantal.shieldcrypt.entities.Conversational;
import com.advantal.shieldcrypt.entities.Message;
import com.advantal.shieldcrypt.ui.ConversationFragment;
import com.advantal.shieldcrypt.ui.XmppActivity;
import com.advantal.shieldcrypt.ui.util.StyledAttributes;
import com.advantal.shieldcrypt.utils.IrregularUnicodeDetector;
import com.advantal.shieldcrypt.utils.MimeUtils;
import com.advantal.shieldcrypt.utils.UIHelper;
import com.advantal.shieldcrypt.utils_pkg.MyApp;
import com.advantal.shieldcrypt.xmpp.Jid;
import com.advantal.shieldcrypt.xmpp.jingle.OngoingRtpSession;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

import org.whispersystems.libsignal.logging.Log;

import java.util.List;

import database.my_database_pkg.db_table.MyAppDataBase;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private final XmppActivity activity;
    private List<Conversation> conversations;
    private OnConversationClickListener listener;

    public ConversationAdapter(XmppActivity activity, List<Conversation> conversations) {//,OnConversationClickListener listener
        this.activity = activity;
        this.conversations = conversations;
    }

    public void setMyUpdatedConversation(List<Conversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversationViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.conversation_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder viewHolder, int position) {
        Conversation conversation = conversations.get(position);
        Log.e("Position:", position + "");
        if (conversation == null) {
            return;
        }
        CharSequence name = conversation.getName();

        viewHolder.binding.statusOnline.setVisibility(View.INVISIBLE);
        viewHolder.binding.conversationImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.groupavatar, null));


        String strs = "";
        String str = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getUser(name.toString());
        if (str != null && !str.equals("")) {
            viewHolder.binding.conversationName.setText(str);
            strs = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getUserProfilePick(name.toString());

            if (strs == null && strs.equals("")) {
                viewHolder.binding.conversationImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.one_person, null));
            }
            Contact contact = conversation.getAccount().getRoster().getContact(conversation.getJid());

            if (contact != null && contact.isActive()) {
                viewHolder.binding.statusOnline.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.statusOnline.setVisibility(View.INVISIBLE);
            }
        } else {
            try {
                String[] arrOfStr = name.toString().split("@");
                if (arrOfStr.length >= 1) {
                    String grpNm = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).groupDao().getGrpNmById(arrOfStr[0]);

                    if (grpNm != null && !grpNm.equals("")) {
                        viewHolder.binding.conversationName.setText(grpNm);
                    } else {
                        viewHolder.binding.conversationName.setText(name.toString());
                    }

                } else {
                    if (name instanceof Jid) {
                        viewHolder.binding.conversationName.setText(IrregularUnicodeDetector.style(activity, (Jid) name));
                    } else {
                        viewHolder.binding.conversationName.setText(name.toString());
                    }
                }

                strs = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).groupDao().getUserProfilePick(viewHolder.binding.conversationName.getText().toString());

                if (strs == null && strs.equals("")) {
                    viewHolder.binding.conversationImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.groupavatar, null));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (strs != null && !strs.equals("")) {
            Glide.with(activity).load(strs).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(viewHolder.binding.conversationImage);
        }

        if (conversation == ConversationFragment.getConversation(activity)) {
            viewHolder.binding.frame.setBackgroundColor(StyledAttributes.getColor(activity, R.attr.color_background_tertiary));
        } else {
            viewHolder.binding.frame.setBackgroundColor(StyledAttributes.getColor(activity, R.attr.color_background_primary));
        }

        Message message = conversation.getLatestMessage();
        final int unreadCount = conversation.unreadCount();
        final boolean isRead = conversation.isRead();
        final Conversation.Draft draft = isRead ? conversation.getDraft() : null;

        if (unreadCount > 0) {
            viewHolder.binding.unreadCount.setVisibility(View.VISIBLE);
            viewHolder.binding.unreadCount.setUnreadCount(unreadCount);
        } else {
            viewHolder.binding.unreadCount.setVisibility(View.GONE);
        }

        if (isRead) {
//            viewHolder.binding.conversationName.setTypeface(null, Typeface.NORMAL);
        } else {
//            viewHolder.binding.conversationName.setTypeface(null, Typeface.BOLD);
        }

        if (draft != null) {
            viewHolder.binding.conversationLastmsgImg.setVisibility(View.GONE);
            viewHolder.binding.conversationLastmsg.setText(draft.getMessage());
            viewHolder.binding.senderName.setText(R.string.draft);
            viewHolder.binding.senderName.setVisibility(View.VISIBLE);

        } else {
            final boolean fileAvailable = !message.isDeleted();
            final boolean showPreviewText;
            if (fileAvailable && (message.isFileOrImage() || message.treatAsDownloadable() || message.isGeoUri())) {
                final int imageResource;
                if (message.isGeoUri()) {
                    imageResource = activity.getThemeResource(R.attr.ic_attach_location, R.drawable.ic_attach_location);
                    showPreviewText = false;
                } else {
                    // TODO move this into static MediaPreview method and use same icons as in
                    // MediaAdapter
                    final String mime = message.getMimeType();
                    if (MimeUtils.AMBIGUOUS_CONTAINER_FORMATS.contains(mime)) {
                        final Message.FileParams fileParams = message.getFileParams();
                        if (fileParams.width > 0 && fileParams.height > 0) {
                            imageResource = activity.getThemeResource(R.attr.ic_attach_videocam, R.drawable.ic_attach_videocam);
                            showPreviewText = false;
                        } else if (fileParams.runtime > 0) {
                            imageResource = activity.getThemeResource(R.attr.ic_attach_record, R.drawable.ic_attach_record);
                            showPreviewText = false;
                        } else {
                            imageResource = activity.getThemeResource(R.attr.ic_attach_document, R.drawable.ic_attach_document);
                            showPreviewText = true;
                        }
                    } else {
                        switch (Strings.nullToEmpty(mime).split("/")[0]) {
                            case "image":
                                imageResource = activity.getThemeResource(R.attr.ic_attach_photo, R.drawable.ic_attach_photo);
                                showPreviewText = false;
                                break;
                            case "video":
                                imageResource = activity.getThemeResource(R.attr.ic_attach_videocam, R.drawable.ic_attach_videocam);
                                showPreviewText = false;
                                break;
                            case "audio":
                                imageResource = activity.getThemeResource(R.attr.ic_attach_record, R.drawable.ic_attach_record);
                                showPreviewText = false;
                                break;
                            default:
                                imageResource = activity.getThemeResource(R.attr.ic_attach_document, R.drawable.ic_attach_document);
                                showPreviewText = true;
                                break;
                        }
                    }
                }
                viewHolder.binding.conversationLastmsgImg.setImageResource(imageResource);
                viewHolder.binding.conversationLastmsgImg.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.conversationLastmsgImg.setVisibility(View.GONE);
                showPreviewText = true;
            }
            final Pair<CharSequence, Boolean> preview = UIHelper.getMessagePreview(activity, message, viewHolder.binding.conversationLastmsg.getCurrentTextColor());
            if (showPreviewText) {
                viewHolder.binding.conversationLastmsg.setText(UIHelper.shorten(preview.first));
            } else {
                viewHolder.binding.conversationLastmsgImg.setContentDescription(preview.first);
            }
            viewHolder.binding.conversationLastmsg.setVisibility(showPreviewText ? View.VISIBLE : View.GONE);
            if (preview.second) {
                if (isRead) {
//                    viewHolder.binding.conversationLastmsg.setTypeface(null, Typeface.ITALIC);
//                    viewHolder.binding.senderName.setTypeface(null, Typeface.NORMAL);
                } else {
//                    viewHolder.binding.conversationLastmsg.setTypeface(null, Typeface.BOLD_ITALIC);
//                    viewHolder.binding.senderName.setTypeface(null, Typeface.BOLD);
                }
            } else {
                if (isRead) {
//                    viewHolder.binding.conversationLastmsg.setTypeface(null, Typeface.NORMAL);
//                    viewHolder.binding.senderName.setTypeface(null, Typeface.NORMAL);
                } else {
//                    viewHolder.binding.conversationLastmsg.setTypeface(null, Typeface.BOLD);
//                    viewHolder.binding.senderName.setTypeface(null, Typeface.BOLD);
                }
            }
            if (message.getStatus() == Message.STATUS_RECEIVED) {
                if (conversation.getMode() == Conversation.MODE_MULTI) {
                    viewHolder.binding.senderName.setVisibility(View.VISIBLE);
                    viewHolder.binding.senderName.setText(UIHelper.getMessageDisplayName(message).split("\\s+")[0] + ':');
                } else {
                    viewHolder.binding.senderName.setVisibility(View.GONE);
                }
            } else if (message.getType() != Message.TYPE_STATUS) {
                viewHolder.binding.senderName.setVisibility(View.GONE);
                viewHolder.binding.senderName.setText(activity.getString(R.string.me) + ':');
            } else {
                viewHolder.binding.senderName.setVisibility(View.GONE);
            }
        }

        final Optional<OngoingRtpSession> ongoingCall;
        if (conversation.getMode() == Conversational.MODE_MULTI) {
            ongoingCall = Optional.absent();
        } else {
            ongoingCall = activity.xmppConnectionService.getJingleConnectionManager().getOngoingRtpConnection(conversation.getContact());
        }

        if (ongoingCall.isPresent()) {
            viewHolder.binding.notificationStatus.setVisibility(View.VISIBLE);
            final int ic_ongoing_call = activity.getThemeResource(R.attr.ic_ongoing_call_hint, R.drawable.ic_phone_in_talk_black_18dp);
            viewHolder.binding.notificationStatus.setImageResource(ic_ongoing_call);
        } else {
            final long muted_till = conversation.getLongAttribute(Conversation.ATTRIBUTE_MUTED_TILL, 0);
            if (muted_till == Long.MAX_VALUE) {
                viewHolder.binding.notificationStatus.setVisibility(View.VISIBLE);
                int ic_notifications_off = activity.getThemeResource(R.attr.icon_notifications_off, R.drawable.ic_notifications_off_black_24dp);
                viewHolder.binding.notificationStatus.setImageResource(ic_notifications_off);
            } else if (muted_till >= System.currentTimeMillis()) {
                viewHolder.binding.notificationStatus.setVisibility(View.VISIBLE);
                int ic_notifications_paused = activity.getThemeResource(R.attr.icon_notifications_paused, R.drawable.ic_notifications_paused_black_24dp);
                viewHolder.binding.notificationStatus.setImageResource(ic_notifications_paused);
            } else if (conversation.alwaysNotify()) {
                viewHolder.binding.notificationStatus.setVisibility(View.GONE);
            } else {
                viewHolder.binding.notificationStatus.setVisibility(View.VISIBLE);
                int ic_notifications_none = activity.getThemeResource(R.attr.icon_notifications_none, R.drawable.ic_notifications_none_black_24dp);
                viewHolder.binding.notificationStatus.setImageResource(ic_notifications_none);
            }
        }

        long timestamp;
        if (draft != null) {
            timestamp = draft.getTimestamp();
        } else {
            timestamp = conversation.getLatestMessage().getTimeSent();
        }
        viewHolder.binding.pinnedOnTop.setVisibility(conversation.getBooleanAttribute(Conversation.ATTRIBUTE_PINNED_ON_TOP, false) ? View.VISIBLE : View.GONE);
        viewHolder.binding.conversationLastupdate.setText(UIHelper.readableTimeDifference(activity, timestamp));


        viewHolder.itemView.setOnClickListener(v -> listener.onConversationClick(v, conversation));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void setConversationClickListener(OnConversationClickListener listener) {
        this.listener = listener;
    }

    public void insert(Conversation c, int position) {
        conversations.add(position, c);
        notifyDataSetChanged();
    }

    public void remove(Conversation conversation, int position) {
        conversations.remove(conversation);
        notifyItemRemoved(position);
    }

    public interface OnConversationClickListener {
        void onConversationClick(View view, Conversation conversation);
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        private final ConversationListRowBinding binding;

        private ConversationViewHolder(ConversationListRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
