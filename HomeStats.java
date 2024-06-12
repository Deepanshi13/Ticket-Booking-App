package com.getepay.smartcitycheckin;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OptionalDataException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class HomeStats extends Fragment implements ApiCallListener, Activity2FragmentInterface, BackerCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FrameLayout view;
    private TextView soldOnes;
    private TextView checkedOnes, tickets_details, current_time;
    private ImageView camera;

    private LinearLayout popup;
    private RelativeLayout popupBack;
    private ListView details;
    private ListView checkins;

    private TextView puname;
    private TextView puid;
    private TextView pudate;
    private ImageView checkinBtn;
    //private ImageView camera;
    private TextView puaddress;
    private TextView puemail;
    private TextView pucity;
    private TextView pucountry;

    private OnFragmentInteractionListener mListener;
    private String scannedData = "";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeStats.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeStats newInstance(String param1, String param2) {
        HomeStats fragment = new HomeStats();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeStats() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HOME", "onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (!mParam1.equals("")){
            String[] resArr = mParam1.split("\\|");
            if (resArr.length > 0) {
                Globals.showProgress(this.getActivity());
                ApiCall check = new ApiCall(Globals.url + "/tc-api/" + Globals.key + "/check_in/"+resArr[resArr.length-1], this, 2);
            }
        }

        translate();

        ApiCall ac = new ApiCall(Globals.url+"/tc-api/"+Globals.key+"/event_essentials", this, 1);
    }

    private void translate(){
        if (view == null) return;
        VerticalTextView sold = (VerticalTextView)view.findViewById(R.id.sold_text);
        VerticalTextView checked = (VerticalTextView)view.findViewById(R.id.checkedin_text);
        sold.setText(Globals.getTranslation("SOLD_TICKETS"));
        checked.setText(Globals.getTranslation("CHECKED_IN_TICKETS"));
    }

    @Override
    public void call(String result, boolean success, int id) {
        Globals.cancelProgressDialog();
        if (success) {
            if (id == 7) {
                populateCheckins(result, success);
            } else {
                try {
                    JSONObject job = new JSONObject(result);
                    if (job.has("checked_tickets") && job.has("sold_tickets")) {
                        checkedOnes.setText("0");
                        soldOnes.setText("0");
                        int checked = job.getInt("checked_tickets");
                        int sold = job.getInt("sold_tickets");
                        Globals.eventName = job.getString("event_name");
                        Globals.eventDate = job.getString("event_date_time");
                        Globals.eventLocation = job.getString("event_location");
                        Globals.checked = checked;
                        Globals.sold = sold;
                        if (job.has("show_attendee_screen")) {
                            Globals.show_attendee_screen = job.getBoolean("show_attendee_screen");
                        } else {
                            Globals.show_attendee_screen = false;
                        }
                        if (job.has("show_at_once")) {
                            Globals.show_at_once = job.getString("show_at_once").equals("1");
                        } else {
                            Globals.show_at_once = false;
                        }
                        checkedOnes.setText(Integer.toString(checked));
                        soldOnes.setText(Integer.toString(sold));
                        tickets_details.setText(Globals.eventName);
                        current_time.setText(getCurrentDateTime());
                    } else if (job.has("checksum") && job.has("status") && job.has("pass")) {
                        if (job.getBoolean("status") && job.getBoolean("pass")) {
                            if (Globals.show_attendee_screen)
                                populatePopup2(job);
                            Globals.showSuccess(this.getActivity(), Globals.show_attendee_screen);
                        } else {
                            Globals.showFailCheck(this.getActivity());
                        }
                    }
                } catch (JSONException jex) {
                    Globals.showFailCheck(this.getActivity());
                    jex.printStackTrace();
                }
            }
        } else {
            Globals.cancelProgressDialog();
        }
    }


    @Override
    public void update() {
        ApiCall ac = new ApiCall(Globals.url+"/tc-api/"+Globals.key+"/event_essentials", this, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = (FrameLayout)inflater.inflate(R.layout.fragment_home_stats, container, false);

        soldOnes = (TextView)view.findViewById(R.id.tickets_sold);
        checkedOnes = (TextView)view.findViewById(R.id.checked_in_tickets);
        tickets_details = (TextView)view.findViewById(R.id.tickets_details);
        current_time = (TextView)view.findViewById(R.id.current_time);
        camera = (ImageView)view.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CAMERA", "rrrrrr");
                ((MainActivity2Activity)getActivity()).doScanner();
                //Globals.showProgress(HomeStats.this.getActivity());
                //ApiCall ac = new ApiCall(Globals.url+"/tc-api/"+Globals.key+"/check_in/17E5A25593-1", HomeStats.this, 8);
            }
        });

//        checkedOnes.setText(Integer.toString(Globals.checked));
        checkedOnes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = List.newInstance(scannedData, "");
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, fragment)
                            .commitAllowingStateLoss();


                }
            }
        });

        soldOnes.setText(Integer.toString(Globals.sold));

        popup = (LinearLayout)view.findViewById(R.id.popup);
        popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
        popup.setVisibility(View.GONE);
        popupBack = (RelativeLayout)view.findViewById(R.id.popup_back);
        popupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closePopup();
            }
        });
        details = (ListView)view.findViewById(R.id.details);
        checkins = (ListView)view.findViewById(R.id.checkins);

        puname = (TextView)view.findViewById(R.id.name);
        puid = (TextView)view.findViewById(R.id.id);
        pudate = (TextView)view.findViewById(R.id.date);
        checkinBtn = (ImageView)view.findViewById(R.id.checkin_btn);
        checkinBtn.setVisibility(View.GONE);
//        checkinBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (popup.getVisibility() == View.VISIBLE){
//                    Globals.showProgress(List.this.getActivity());
//                    ApiCall ac = new ApiCall(Globals.url+"/tc-api/"+Globals.key+"/check_in/"+puid.getText(), List.this, 8);
//                }
//            }
//        });

        translate();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            translate();
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void populateCheckins(String result, boolean success){
        Globals.cancelProgressDialog();
        if (success){
            try{
                ArrayList<String> arrList = new ArrayList<String>();
                JSONArray arr = new JSONArray(result);
                for (int i=0;i<arr.length();i++){
                    JSONObject obj = arr.getJSONObject(i);
                    JSONObject data = obj.getJSONObject("data");
                    arrList.add(data.getString("date_checked")+ " - " + data.getString("status"));
                }

                ListView list = (ListView)view.findViewById(R.id.checkins);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.simple_list_item, arrList);
                list.setAdapter(adapter);

            }catch (JSONException jex){
                jex.printStackTrace();
            }
        }
    }

    private void closePopup(){
        popup.setVisibility(View.GONE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        MainActivity2Activity.canGoBack = true;
        MainActivity2Activity.backerCallback = null;
    }

    @Override
    public void call() {
        closePopup();
    }

    private void populatePopup2(JSONObject obj){

        popup.setVisibility(View.VISIBLE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        MainActivity2Activity.canGoBack = false;
        MainActivity2Activity.backerCallback = this;

        String code = "";

        ArrayList<JSONArray> dlist = new ArrayList<JSONArray>();
        try {

            puname.setText(Html.fromHtml(obj.getString("name")));
            puid.setText(Html.fromHtml(obj.getString("checksum")));
            pudate.setText(Html.fromHtml(obj.getString("payment_date")));

            code = obj.getString("checksum");

            translate();

            JSONArray arr = obj.getJSONArray("custom_fields");
            for (int i=0;i<arr.length(); i++){
                JSONArray one = arr.getJSONArray(i);
                if (one.length()>1)
                    dlist.add(one);
            }
        }catch (JSONException jex){
            jex.printStackTrace();
        }
        DetailsListAdapter dapter = new DetailsListAdapter(this.getActivity(), dlist);
        ListView details = (ListView)view.findViewById(R.id.details);
        details.setAdapter(dapter);

        Globals.showProgress(getActivity());
        ApiCall ac = new ApiCall(Globals.url+"/tc-api/"+Globals.key+"/ticket_checkins/"+code, this, 7);
    }
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

}
