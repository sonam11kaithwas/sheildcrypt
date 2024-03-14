package com.advantal.shieldcrypt.tabs_pkg.tab_fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.listener.OnLoadMoreListener;
import com.advantal.shieldcrypt.network_pkg.MainViewModel;
import com.advantal.shieldcrypt.network_pkg.RequestApis;
import com.advantal.shieldcrypt.network_pkg.Resource;
import com.advantal.shieldcrypt.network_pkg.ResponceModel;
import com.advantal.shieldcrypt.network_pkg.Status;
import com.advantal.shieldcrypt.sip.SharedPrefrence;
import com.advantal.shieldcrypt.sip.utils.Utils;
import com.advantal.shieldcrypt.tabs_pkg.model.datacallmodel.CallLogsResponse;
import com.advantal.shieldcrypt.tabs_pkg.model.datacallmodel.ContentItem;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.advantal.shieldcrypt.utils_pkg.AppUtills;
import com.advantal.shieldcrypt.utils_pkg.MyApp;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import net.gotev.sipservice.SipAccount;
import net.gotev.sipservice.SipServiceCommand;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import database.my_database_pkg.db_table.MyAppDataBase;

@AndroidEntryPoint
public class NewCallLogFragment extends Fragment {

    private RecyclerView callsRecyclerView;
    private List<ContentItem> mUsers = new ArrayList<>();
    private UserAdapter mUserAdapter;
    int page = 0;
    int size = 20;
    public SharedPrefrence share;
    private MainViewModel mainViewModel;
    private TextView msg;
    private ProgressBar progressBarMain;
    private SwipeRefreshLayout pull_refreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_call_list, container, false);
        share = SharedPrefrence.getInstance(requireActivity());
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        msg = (TextView)rootView.findViewById(R.id.msg);
        callsRecyclerView = rootView.findViewById(R.id.callsRecyclerView);
        pull_refreshLayout = rootView.findViewById(R.id.pull_refreshLayout);
        progressBarMain = rootView.findViewById(R.id.progressBarMain);

        callsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mUserAdapter = new UserAdapter();
        callsRecyclerView.setAdapter(mUserAdapter);
        progressBarMain.setIndeterminate(true);

        pull_refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                loadMore();
                pull_refreshLayout.setRefreshing(false);
            }
        });

        page = 0;
        loadMore();

        mUserAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                mUsers.add(null);
                if (mUserAdapter!=null){
                    mUserAdapter.notifyItemInserted(mUsers.size() - 1);
                }
                page++;
                loadMore();
                //Load more data for reyclerview
            }
        });

        mainViewModel.getResponceCallBack().observeForever(new Observer<Resource<ResponceModel>>() {
            @Override
            public void onChanged(Resource<ResponceModel> responceModelResource) {
                if (page==0){
                    if (mUsers==null){
                        mUsers = new ArrayList<>();
                    }
                    mUsers.clear();
                }
                if (responceModelResource.getStatus().equals(Status.SUCCESS)) {
                    AppUtills.Companion.closeProgressDialog();
                    if (responceModelResource.getData().getRequestCode() == RequestApis.CALL_LOGS_APIS) {
                        TypeToken<CallLogsResponse> token = new TypeToken<CallLogsResponse>() {};
                        CallLogsResponse mUsersNew = new Gson().fromJson(responceModelResource.getData().getData(), token.getType());

                        if (mUsersNew!=null && mUsersNew.getClmp()!=null && mUsersNew.getClmp().size()>0){
                            progressBarMain.setVisibility(View.GONE);
                            if (page>0){
                                //Remove loading item
                                mUsers.remove(mUsers.size() - 1);
                                if (mUserAdapter!=null){
                                    mUserAdapter.notifyItemRemoved(mUsers.size());
                                }
                            }
                            mUsers.addAll(mUsersNew.getClmp());
                            if (mUserAdapter!=null){
                                mUserAdapter.notifyDataSetChanged();
                                mUserAdapter.setLoaded();
                            }

                            if (mUserAdapter!=null && mUserAdapter.getItemCount()>0){
                                msg.setVisibility(View.GONE);
                            }
                        } else {
                            mUsers.remove(mUsers.size() - 1);
                            if (mUserAdapter!=null){
                                mUserAdapter.notifyItemRemoved(mUsers.size());
                            }
                            if (mUserAdapter!=null){
                                mUserAdapter.notifyDataSetChanged();
                                mUserAdapter.setLoaded();
                            }
                        }
                    }
                } else if (responceModelResource.getStatus().equals(Status.LOADING)) {
                    AppUtills.Companion.closeProgressDialog();
                } else if (responceModelResource.getStatus().equals(Status.ERROR)) {

                }
            }
        });

        return rootView;
    }

    public void loadMore() {
        CallListFragment.CallLogBeans callLogBeans= new CallListFragment.CallLogBeans(page, size,
                share.getValue(AppConstants.loggedInUserNumber));
        mainViewModel.featchDataFromServerWithoutAuth(
                new Gson().toJson(callLogBeans), RequestApis.callLogs, RequestApis.CALL_LOGS_APIS
        );
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView display_name_tv, call_details;
        public ImageView call_img;
        public AppCompatImageView audio_vedio_call_img;

        public UserViewHolder(View itemView) {
            super(itemView);
            display_name_tv = (TextView) itemView.findViewById(R.id.display_name_tv);
            call_details = (TextView) itemView.findViewById(R.id.call_details);
            call_img = (ImageView) itemView.findViewById(R.id.call_img);
            audio_vedio_call_img = (AppCompatImageView) itemView.findViewById(R.id.audio_vedio_call_img);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        public UserAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) callsRecyclerView.getLayoutManager();
            callsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @Override
        public int getItemViewType(int position) {
            return mUsers.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(requireActivity()).inflate(R.layout.calls_list_item, parent, false);
                return new UserViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(requireActivity()).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof UserViewHolder) {
                if (mUsers!=null){
                    ContentItem user = mUsers.get(position);
                    if (user!=null){
                        UserViewHolder userViewHolder = (UserViewHolder) holder;
                        if (user.getSrc_cnam()!=null && !user.getSrc_cnam().isEmpty()) {
                            userViewHolder.display_name_tv.setText(user.getSrc_cnam());
                        } else {
                            if (user.getSrc() != null && !user.getSrc().isEmpty()) {
                                String nm =
                                        MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance())
                                        .contactDao().getUserNameByPhone(user.getSrc());
                                if (nm != null && !nm.isEmpty()) userViewHolder.display_name_tv.setText(nm);
                                else  userViewHolder.display_name_tv.setText(""+user.getSrc());
                            } else userViewHolder.display_name_tv.setText(""+user.getSrc());
                        }

                        if (user.getSrc() != null && !user.getSrc().isEmpty()) {
                            userViewHolder.itemView.setVisibility(View.VISIBLE);
                        } else {
                            userViewHolder.itemView.setVisibility(View.GONE);
                        }

                        if (user.getType()!=null && !user.getType().isEmpty()){
                            userViewHolder.call_img.setVisibility(View.VISIBLE);
                            if (user.getType().toString().equals("OUTGOING")) {
                                userViewHolder.call_img.setImageResource(R.drawable.ic_out_going_call);
                            } else if (user.getType().toString().equals("INCOMING")) {
                                userViewHolder.call_img.setImageResource(R.drawable.ic_in_coming_call);
                            } else if (user.getType().toString().equals("MISSED")) {
                                userViewHolder.call_img.setImageResource(R.drawable.ic_miss_call);
                            } else {
                                userViewHolder.call_img.setVisibility(View.GONE);
                            }
                        } else {
                            userViewHolder.call_img.setVisibility(View.GONE);
                        }

                        if (user.getCalldate() != null && !user.getCalldate().isEmpty()) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                            try {
                                Date date = stringToDate(user.getCalldate());
                                String dateTime = dateFormat.format(date);
                                Log.e("itemListStatus ", " CurrentDateTime--> " + dateTime);
                                userViewHolder.call_details.setText(""+dateTime);
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        userViewHolder.audio_vedio_call_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clickedOnCall(user);
                            }
                        });
                    }
                }
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        private Date stringToDate(String dtStart) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if (dtStart != null && !dtStart.isEmpty()) {
                    date = format.parse(dtStart);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }

        @Override
        public int getItemCount() {
            return mUsers == null ? 0 : mUsers.size();
        }

        public void setLoaded() {
            isLoading = false;
        }
    }

    public void clickedOnCall(ContentItem selectedRow) {
        if (selectedRow!=null && selectedRow.getSrc()!=null && !selectedRow.getSrc().isEmpty()){
            initiateCall(selectedRow.getSrc());
        }
    }

    public void initiateCall(String number) {
        Utils.hideKeyBoard(requireActivity());
        if (!Utils.checkInternetConn(requireActivity())) {
            Toast.makeText(
                    requireActivity(), getString(R.string.internet_check), Toast.LENGTH_SHORT
            ).show();
        } else {
            if (checkValidation(number)) {
                if (SipAccount.activeCalls.size() > 0) {
                    Toast.makeText(
                            requireActivity(),
                            getString(R.string.you_have_already_on_call),
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    onCall(number);
                }
            }
        }
    }

    public Boolean checkValidation(String number) {
        if (number.equals("")) {
            Toast.makeText(
                    requireActivity(), getString(R.string.please_enter_mobile), Toast.LENGTH_SHORT
            ).show();
            return false;
        } else {
            return true;
        }
    }

    public void onCall(String number) {
        String id = share.getValue(SharedPrefrence.SIPACCOUNTID);
        //AppConstants.TYPE = "CALL";
        /*if (number == null && number!!.isEmpty()) {
            number = "*9000"
        }*/
        Log.e("go id", "dsda id " + id);
        Log.e("c", "no $number " + number);
        SipServiceCommand.makeCall(requireActivity(), id, number);
    }
}
