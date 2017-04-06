package com.eighteengray.procamera;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.eighteengray.procamera.activity.AlbumActivity;
import com.eighteengray.procamera.activity.GpuFilterActivity;
import com.eighteengray.procamera.activity.SettingActivity;
import com.eighteengray.procamera.common.Constants;
import com.eighteengray.procamera.fragment.Camera2Fragment;
import com.eighteengray.procamera.fragment.RecordVideoFragment;
import com.eighteengray.procamera.widget.baserecycler.BaseRecyclerAdapter;
import com.eighteengray.procamera.widget.baserecycler.BaseRecyclerViewHolder;
import com.eighteengray.procamera.widget.dialogfragment.SetDelayTimeDialogFragment;
import com.eighteengray.procamera.widget.dialogfragment.SetRatioDialogFragment;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.eighteengray.procamera.R.id.cameraTextureView;
import static com.eighteengray.procamera.R.id.ll_histogram;
import static com.eighteengray.procamera.R.id.recordTextureView;



public class MainActivity extends FragmentActivity
{
    //上部
    @BindView(R.id.iv_flash_camera)
    ImageView iv_flash_camera;
    @BindView(R.id.tv_mode_gpufileter)
    TextView tv_mode_gpufileter;
    @BindView(R.id.iv_switch_camera)
    ImageView iv_switch_camera;

    //中部相机拍照录像区域
    @BindView(R.id.vp_textureview)
    public ViewPager vp_textureview;
    FragmentPagerAdapter fragmentPagerAdapter;
    List<Fragment> fragments = new ArrayList<>();
    Camera2Fragment camera2Fragment;
    RecordVideoFragment recordVideoFragment;
    int currentPage = 0;

    @BindView(R.id.ll_histogram)
    LinearLayout ll_histogram;

    //中下部
    @BindView(R.id.rl_middle_bottom_menu)
    RelativeLayout rl_middle_bottom_menu;
    @BindView(R.id.iv_extra_camera)
    ImageView iv_extra_camera;
    @BindView(R.id.recyclerview_mode)
    RecyclerView recyclerview_mode;
    @BindView(R.id.iv_gpufilter_camera)
    ImageView iv_gpufilter_camera;

    //下部
    @BindView(R.id.iv_album_camera)
    ImageView iv_album_camera;
    @BindView(R.id.iv_ratio_camera)
    ImageView iv_ratio_camera;
    @BindView(R.id.iv_shutter_camera)
    ImageView iv_shutter_camera;
    @BindView(R.id.iv_shutter_record)
    ImageView iv_shutter_record;
    @BindView(R.id.iv_delay_shutter)
    ImageView iv_delay_shutter;
    @BindView(R.id.iv_setting_camera)
    ImageView iv_setting_camera;

    boolean isRecording = false;
    boolean isFront = false;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //去掉status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

        setContentView(R.layout.activity_camera);
        initView();
        ButterKnife.bind(this);
    }


    private void initView()
    {
        camera2Fragment = new Camera2Fragment();
        recordVideoFragment = new RecordVideoFragment();
        fragments.add(camera2Fragment);
        fragments.add(recordVideoFragment);
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {
            @Override
            public Fragment getItem(int position)
            {
                return fragments.get(position);
            }

            @Override
            public int getCount()
            {
                return fragments.size();
            }
        };
        vp_textureview = (ViewPager) findViewById(R.id.vp_textureview);
        vp_textureview.setAdapter(fragmentPagerAdapter);

        //RecyclerView的三种模式，对应三种UI，三种行为及其对应的presenter，lib中提供底层实现方法，相当于model层。
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview_mode = (RecyclerView) findViewById(R.id.recyclerview_mode);
        recyclerview_mode.setLayoutManager(layoutManager);
        BaseRecyclerAdapter<String> adapter = new BaseRecyclerAdapter<String>(R.layout.item_recycler_main)
        {
            @Override
            public void setData2ViewR(BaseRecyclerViewHolder baseRecyclerViewHolder, final String item)
            {
                TextView textView = baseRecyclerViewHolder.getViewById(R.id.tv_item_recycler);
                textView.setText(item);
                textView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        if (item.equals("照片"))
                        {
                            updateCameraView();
                        } else if (item.equals("视频"))
                        {
                            updateRecordView();
                        }
                    }
                });
            }
        };
        recyclerview_mode.setAdapter(adapter);
        List<String> list = new ArrayList<>();
        list.add("照片");
        list.add("视频");
        adapter.setData(list);
    }


    @OnClick({R.id.iv_flash_camera, R.id.iv_switch_camera,
            R.id.iv_extra_camera, R.id.iv_gpufilter_camera,
            R.id.iv_album_camera, R.id.iv_ratio_camera, R.id.iv_shutter_camera, R.id.iv_delay_shutter, R.id.iv_setting_camera})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_flash_camera:
                camera2Fragment.setFlashMode(0);
                recordVideoFragment.setFlashMode(1);
                break;

            case R.id.iv_switch_camera:
                if(isFront)
                {
                    camera2Fragment.switchCamera(true);
                    recordVideoFragment.switchCamera(true);
                }
                else
                {
                    camera2Fragment.switchCamera(false);
                    recordVideoFragment.switchCamera(false);
                }
                break;

            case R.id.iv_gpufilter_camera:
                Intent intent1 = new Intent(MainActivity.this, GpuFilterActivity.class);
                startActivityForResult(intent1, Constants.GPUFILTER);
                break;

            case R.id.iv_album_camera:
                Intent intent2 = new Intent(MainActivity.this, AlbumActivity.class);
                startActivity(intent2);
                break;

            case R.id.iv_ratio_camera:
                camera2Fragment.setRatio();
                recordVideoFragment.setRatio();
                break;

            case R.id.iv_shutter_camera:
                camera2Fragment.takePicture();
                break;

            case R.id.iv_shutter_record:
                if(!isRecording)
                {
                    recordVideoFragment.startRecordVideo();
                    Toast.makeText(MainActivity.this, "正在录制", Toast.LENGTH_SHORT).show();
                }
                else {
                    recordVideoFragment.stopRecordVideo();
                    Toast.makeText(MainActivity.this, "录制结束", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iv_delay_shutter:
                camera2Fragment.delayShutter();
                break;

            case R.id.iv_setting_camera:
                Intent intent3 = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent3);
                break;
        }
    }



    private void updateCameraView()
    {

        ll_histogram.setVisibility(View.VISIBLE);
        iv_shutter_camera.setVisibility(View.VISIBLE);
        iv_shutter_record.setVisibility(View.GONE);
    }


    private void updateRecordView()
    {

        ll_histogram.setVisibility(View.GONE);
        iv_shutter_camera.setVisibility(View.GONE);
        iv_shutter_record.setVisibility(View.VISIBLE);
    }


    //点击事件和回接逐渐用RxAndroid替代。
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.GPUFILTER)
        {
            camera2Fragment.setGpuFilter();
            recordVideoFragment.setGpuFilter();
        }
    }

}