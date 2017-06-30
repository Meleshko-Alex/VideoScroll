package meleshko.com.videoscroll;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

public class VideoViewHolder extends RecyclerView.ViewHolder{

    public final VideoPlayerView mPlayer;
    public final TextView mTitle;
    public final ImageView mCover;
    public final ImageView mPause;
    public final ImageView mPlay;

    public VideoViewHolder(View view) {
        super(view);
        mPlayer = (VideoPlayerView) view.findViewById(R.id.player);
        mTitle = (TextView) view.findViewById(R.id.title);
        mCover = (ImageView) view.findViewById(R.id.cover);
        mPause = (ImageView) view.findViewById(R.id.iv_pause);
        mPlay = (ImageView) view.findViewById(R.id.iv_play);
    }
}
