package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.locationsharing.R;


public class ViewImageFragment extends Fragment {

    private View mView;
    private WebView webView;

    private String imageUrl;

    public ViewImageFragment() {
    }

    public static ViewImageFragment newInstance(String param1, String param2) {
        ViewImageFragment fragment = new ViewImageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_view_image, container, false);

        webView = mView.findViewById(R.id.viewImageFrag_webView);

        Bundle bundle = this.getArguments();
        if(bundle != null){
            imageUrl = bundle.getString("ImageUrl");
        }

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);

        webView.loadUrl(imageUrl);
        return mView;
    }
}