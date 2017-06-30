package meleshko.com.videoscroll;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AbsListView;

import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.visibility_utils.calculator.DefaultSingleItemCalculatorCallback;
import com.volokh.danylo.visibility_utils.calculator.ListItemsVisibilityCalculator;
import com.volokh.danylo.visibility_utils.calculator.SingleListViewItemActiveCalculator;
import com.volokh.danylo.visibility_utils.scroll_utils.ItemsPositionGetter;
import com.volokh.danylo.visibility_utils.scroll_utils.RecyclerViewItemPositionGetter;

import java.io.IOException;
import java.util.ArrayList;

import meleshko.com.videoscroll.items.BaseVideoItem;
import meleshko.com.videoscroll.items.ItemFactory;

public class MainActivity extends AppCompatActivity {

    private ArrayList<BaseVideoItem> mList = new ArrayList<>();

    /**
     * Only the one (most visible) view should be active (and playing).
     * To calculate visibility of views we use {@link SingleListViewItemActiveCalculator}
     */
    private final ListItemsVisibilityCalculator mVideoVisibilityCalculator =
            new SingleListViewItemActiveCalculator(new DefaultSingleItemCalculatorCallback(), mList);

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    /**
     * ItemsPositionGetter is used by {@link ListItemsVisibilityCalculator} for getting information about
     * items position in the RecyclerView and LayoutManager
     */
    private ItemsPositionGetter mItemsPositionGetter;

    /**
     * Here we use {@link SingleVideoPlayerManager}, which means that only one video playback is possible.
     */
    private final VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

    private int mScrollState = AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    private VideoRecyclerViewAdapter videoRecyclerViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
           /* mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_1.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));

            mList.add(ItemFactory.createItemFromAsset("video_sample_1.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_1.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));

            mList.add(ItemFactory.createItemFromAsset("video_sample_1.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_1.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
            mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //View rootView = inflater.inflate(R.layout.fragment_video_recycler_view, container, false);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_video_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        videoRecyclerViewAdapter = new VideoRecyclerViewAdapter(mVideoPlayerManager, this, mList);

        mRecyclerView.setAdapter(videoRecyclerViewAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                mScrollState = scrollState;
                if(scrollState == RecyclerView.SCROLL_STATE_IDLE && !mList.isEmpty()){

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(!mList.isEmpty()){
                    mVideoVisibilityCalculator.onScroll(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition() - mLayoutManager.findFirstVisibleItemPosition() + 1,
                            mScrollState);
                }

                int lastVisibleItemPosition = 0;
                int totalItemCount = mLayoutManager.getItemCount();

                lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                // If the total item count is zero and the previous isn't, assume the
                // list is invalidated and should be reset back to initial state
                if (totalItemCount < previousTotalItemCount) {
                    currentPage = startingPageIndex;
                    previousTotalItemCount = totalItemCount;
                    if (totalItemCount == 0) {
                        loading = true;
                    }
                }
                // If it’s still loading, we check to see if the dataset count has
                // changed, if so we conclude it has finished loading and update the current page
                // number and total item count.
                if (loading && (totalItemCount > previousTotalItemCount)) {
                    loading = false;
                    previousTotalItemCount = totalItemCount;
                }

                // If it isn’t currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                // threshold should reflect how many total columns there are too
                if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                    currentPage++;
                    onLoadMore(currentPage, totalItemCount, recyclerView);
                    loading = true;
                }
            }
        });
        mItemsPositionGetter = new RecyclerViewItemPositionGetter(mLayoutManager, mRecyclerView);

    }

    @Override
    public void onResume() {
        super.onResume();
        if(!mList.isEmpty()){
            // need to call this method from list view handler in order to have filled list

            mRecyclerView.post(new Runnable() {
                @Override
                public void run() {

                    mVideoVisibilityCalculator.onScrollStateIdle(
                            mItemsPositionGetter,
                            mLayoutManager.findFirstVisibleItemPosition(),
                            mLayoutManager.findLastVisibleItemPosition());

                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // we have to stop any playback in onStop
        mVideoPlayerManager.resetMediaPlayer();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
        ArrayList<BaseVideoItem> newList = createContactsList(10, page);
        final int curSize = videoRecyclerViewAdapter.getItemCount();
        mList.addAll(newList);

        view.post(new Runnable() {
            @Override
            public void run() {
                videoRecyclerViewAdapter.notifyItemRangeInserted(curSize, mList.size() - 1);
            }
        });
    }

    public  ArrayList<BaseVideoItem> createContactsList(int numContacts, int offset) {
        ArrayList<BaseVideoItem> mList = new ArrayList<>();

        for (int i = 1; i <= numContacts; i++) {
            try {
                mList.add(ItemFactory.createItemFromAsset("video_sample_2.mp4", R.mipmap.ic_launcher, this, mVideoPlayerManager));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mList;
    }
}
