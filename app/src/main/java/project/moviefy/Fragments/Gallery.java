package project.moviefy.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import project.moviefy.Data.IMDBMovieOrSeriestInfo;
import project.moviefy.Data.IMDBPersontInfo;
import project.moviefy.Data.IMDBSubjectInternalInfo;
import project.moviefy.R;

public class Gallery extends Fragment implements View.OnClickListener{

    IMDBSubjectInternalInfo info;
    ArrayList<String> gallery;
    ArrayList<String> largeGallery;
    int pageCount;
    GridLayout imgGrid;
    Button btnNext;
    Button btnPrevious;
    int currentPage;
    RelativeLayout galleryBackground;
    ImageView largeImage;
    Button btnClose;
    TextView pageNr;
    String url;

    public Gallery(IMDBSubjectInternalInfo info, String url){
        this.info = info;
        gallery = new ArrayList<>();
        pageCount = 1;
        currentPage = 1;
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        getActivity().setTitle("Gallery");
        imgGrid = rootView.findViewById(R.id.img_grid);
        btnNext = rootView.findViewById(R.id.btn_gallery_next);
        btnPrevious = rootView.findViewById(R.id.btn_gallery_previous);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        galleryBackground = rootView.findViewById(R.id.gallery_background);
        largeImage = rootView.findViewById(R.id.gallery_large_img);
        btnClose = rootView.findViewById(R.id.btn_gallery_close);
        btnClose.setOnClickListener(this);
        pageNr = rootView.findViewById(R.id.gallery_page_nr);
        importImgs(url);

        return rootView;
    }

    private void importImgs(final String url) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Document doc = Jsoup.connect(url).get();
                    Elements wrapper = doc.select("span.page_list");
                    pageCount = 1;
                    pageCount += wrapper.select("a").size() / 2;
                    if(info.kind == IMDBSubjectInternalInfo.Kind.PERSON) {
                        gallery = new IMDBPersontInfo().getGallery(info, currentPage);
                        largeGallery = new IMDBPersontInfo().getLargeGallery(info, currentPage);
                    } else {
                        gallery = new IMDBMovieOrSeriestInfo().getGallery(info, currentPage);
                        largeGallery = new IMDBMovieOrSeriestInfo().getLargeGallery(info, currentPage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for(int i = 0; i < gallery.size(); i++){
                                ImageView imgView = (ImageView) imgGrid.getChildAt(i);
                                String imgUrl = gallery.get(i);
                                Picasso.with(getActivity()).load(imgUrl).into(imgView);
                            }
                            setOnClicks();
                            btnNext.setVisibility(View.VISIBLE);
                            btnNext.setClickable(true);

                            btnPrevious.setVisibility(View.VISIBLE);
                            btnNext.setClickable(true);

                            pageNr.setVisibility(View.VISIBLE);
                            pageNr.setText(String.valueOf(currentPage));
                        }
                    });
            }
        }).start();

    }

    private void setOnClicks(){
        for (int i = 0; i < gallery.size(); i++) {
            ImageView imgView = (ImageView) imgGrid.getChildAt(i);
            imgView.setOnClickListener(this);
            imgView.setTag(largeGallery.get(i));
        }
    }

    private void enableOnClicks(){
        for (int i = 0; i < gallery.size(); i++) {
            ImageView imgView = (ImageView) imgGrid.getChildAt(i);
            imgView.setClickable(true);
        }
        btnNext.setClickable(true);
        btnPrevious.setClickable(true);
    }

    private void disableOnClicks(){
        for (int i = 0; i < gallery.size(); i++) {
            ImageView imgView = (ImageView) imgGrid.getChildAt(i);
            imgView.setClickable(false);
        }
        btnNext.setClickable(false);
        btnPrevious.setClickable(true);
    }


    @Override
    public void onClick(final View view) {

        if(view.getId() == btnClose.getId()){
            enableOnClicks();
            galleryBackground.setVisibility(View.INVISIBLE);
            largeImage.setImageResource(R.mipmap.ic_actors);
        } else if (view.getId() == btnNext.getId()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    disableOnClicks();
                    btnNext.setVisibility(View.INVISIBLE);
                    btnPrevious.setVisibility(View.INVISIBLE);
                    if (currentPage < pageCount)
                        currentPage++;
                    if (currentPage <= pageCount) {
                        importImgs(url);
                    }
                }
            });
        } else if(view.getId() == btnPrevious.getId()){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentPage > 1) {
                        disableOnClicks();
                        btnNext.setVisibility(View.INVISIBLE);
                        btnPrevious.setVisibility(View.INVISIBLE);
                        currentPage--;
                        importImgs(url);
                    }
                }
            });


        } else {
            new Thread(new Runnable() {
                public void run() {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            galleryBackground.setVisibility(View.VISIBLE);
                        }
                    });

                    String _url = "http://www.imdb.com" + view.getTag().toString();
                    final String url = _url.substring(0, _url.indexOf("?"));
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(url).get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Elements wrapper = doc.select("[itemprop=image]");
                    final String imgUrl = wrapper.attr("content");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(getActivity()).load(imgUrl).into(largeImage);
                        }
                    });
                }
            }).start();
        }
    }


}
