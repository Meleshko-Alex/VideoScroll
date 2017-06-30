package meleshko.com.videoscroll;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;

import java.util.List;

import meleshko.com.videoscroll.items.BaseVideoItem;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private final VideoPlayerManager mVideoPlayerManager;
    private final List<BaseVideoItem> mList;
    private final Context mContext;

    public VideoRecyclerViewAdapter(VideoPlayerManager videoPlayerManager, Context context, List<BaseVideoItem> list){
        mVideoPlayerManager = videoPlayerManager;
        mContext = context;
        mList = list;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        BaseVideoItem videoItem = mList.get(position);
        View resultView = videoItem.createView(viewGroup, mContext.getResources().getDisplayMetrics().widthPixels);
        final VideoViewHolder mVideoViewHolder = new VideoViewHolder(resultView);
        mVideoViewHolder.mPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mVideoViewHolder.mPause.getVisibility() == View.GONE && mVideoViewHolder.mPlay.getVisibility() == View.VISIBLE) {
                    mVideoViewHolder.mPlay.setVisibility(View.GONE);
                    mVideoViewHolder.mPause.setVisibility(View.VISIBLE);
                } else if (mVideoViewHolder.mPause.getVisibility() == View.VISIBLE && mVideoViewHolder.mPlay.getVisibility() == View.GONE) {
                    mVideoViewHolder.mPlay.setVisibility(View.VISIBLE);
                    mVideoViewHolder.mPause.setVisibility(View.GONE);
                    mVideoPlayerManager.resetMediaPlayer();
                } else {
                    mVideoViewHolder.mPause.setVisibility(View.VISIBLE);
                }
            }
        });
        return mVideoViewHolder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder viewHolder, int position) {
        BaseVideoItem videoItem = mList.get(position);
        videoItem.update(position, viewHolder, mVideoPlayerManager);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}