package com.getepay.smartcitycheckin;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class List extends Fragment implements ApiCallListener, BackerCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment List.
     */
    // TODO: Rename and change types and number of parameters
    public static List newInstance(String param1, String param2) {
        List fragment = new List();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public List() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (!mParam1.equals("")){
            String[] resArr = mParam1.split("\\|");
            if (resArr.length > 0) {
                Globals.showProgress(this.getActivity());
                ApiCall check = new ApiCall(Globals.url + "/tc-api/" + Globals.key + "/check_in/"+resArr[resArr.length-1], this, 8);
            }
        }

    }

    private FrameLayout view;
    private ListView lista;
    private EditText search;
    private ImageView cameraBtn;

    private LinearLayout popup;
    private RelativeLayout popupBack;
    private ListView details;
    private ListView checkins;

    private TextView puname;
    private TextView puid;
    private TextView pudate;
    private ImageView checkinBtn;
    private ImageView camera;
    private TextView puaddress;
    private TextView puemail;
    private TextView pucity;
    private TextView pucountry;


    private ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
    private ArrayList<JSONObject> currList = new ArrayList<JSONObject>();
    private ListAdapter adapter;

    private int page = 1;
    private int maxPage = -1;

    private int total=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view  = (FrameLayout) inflater.inflate(R.layout.fragment_list, container, false);
        search = (EditText) view.findViewById(R.id.search);
        cameraBtn = (ImageView) view.findViewById(R.id.camera);
        lista = (ListView)view.findViewById(R.id.lista);

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
        camera = (ImageView)view.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CAMERA", "rrrrrr");
                ((MainActivity2Activity)getActivity()).doScanner();
            }
        });
        checkinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popup.getVisibility() == View.VISIBLE){
                    Globals.showProgress(List.this.getActivity());
                    ApiCall ac = new ApiCall(Globals.url+"/tc-api/"+Globals.key+"/check_in/"+puid.getText(), List.this, 8);
                    Log.d("CAMERA", ""+Globals.url+"/tc-api/"+Globals.key+"/check_in/"+puid.getText());
                }
            }
        });

        translate();
        //setupLists();

        page = 1;
        maxPage = -1;
        total = 0;

        adapter = new ListAdapter(this.getActivity(), currList);
        lista.setAdapter(adapter);

        lista.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                return;
//                if (i2 <= 0 || i2 <= 0) return;
//                //Log.d("PERCENT", Float.toString((float) (i + i1) / (float) (i2)));
//                String searchText = search.getText().toString().toUpperCase();
//                if (!searchText.equals("")) return;
//                if (/*(i + i1) / (i2) >= 1*/i+i1 >= i2 && !loadingAdditionl && !noMore) {
//                    Globals.showProgress(getActivity());
//                    loadingAdditionl = true;
//                    Log.d("LIST PAGE", Integer.toString(page));
//                    String str = search.getText().toString().toUpperCase();
//                    ApiCall ac = new ApiCall(urlpart + Integer.toString(page) + "/" + str, List.this, 1);
//                }
            }
        });

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                populatePopup(currList.get(i));
            }
        });

        search.setHint(Globals.getTranslation("SEARCH"));
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                performSearch();
                if (i== KeyEvent.FLAG_EDITOR_ACTION || i == KeyEvent.KEYCODE_ENTER){
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        Globals.showProgress(this.getActivity());
        urlpart = Globals.url+"/tc-api/"+Globals.key+"/tickets_info/"+Integer.toString(/*Globals.sold<=50? Globals.sold : */50)+"/";
        ApiCall ac = new ApiCall(urlpart+Integer.toString(Globals.show_at_once? 1 : page), this, 1);

        return view;
    }

    private void setupLists() {
    }



    private void populatePopup(JSONObject obj){

        popup.setVisibility(View.VISIBLE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
        MainActivity2Activity.canGoBack = false;
        MainActivity2Activity.backerCallback = this;

        String code = "";

        ArrayList<JSONArray> dlist = new ArrayList<JSONArray>();
        try {

            puname.setText(Html.fromHtml(obj.getString("buyer_first")+" "+obj.getString("buyer_last")));
            puid.setText(Html.fromHtml(obj.getString("transaction_id")));
            pudate.setText(Html.fromHtml(obj.getString("payment_date")));

            code = obj.getString("transaction_id");

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

    @Override
    public void call() {
        closePopup();
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

    private String getEmailFromObj(JSONObject obj){
        String ret = "N/A";
        try{
            JSONArray arr = obj.getJSONArray("custom_fields");
            for (int i=0;i<arr.length();i++){
                JSONArray item = arr.getJSONArray(i);
                if (item.length()==2) {
                    if (item.getString(0).equals("Buyer E-mail")) {
                        return item.getString(1);
                    }
                }
            }
        }catch (JSONException jex){
            jex.printStackTrace();
        }
        return ret;
    }

    private void performSearch() {
        if (loadingAdditionl) return;
        String str = search.getText().toString().toUpperCase();
        //noMore = false;
        //loadingAdditionl = true;
        //page = 1;
        //Globals.showProgress(getActivity());

        ArrayList<JSONObject> tmp = new ArrayList<>();
        //only local search; results from api call will be added when fetched
        if (!str.isEmpty()) {
            for (int i = 0; i < initialList.size(); i++) {
                JSONObject json = initialList.get(i);
                try {
                    //String name = json.getString("buyer_first").toUpperCase();
                    //String lastName = json.getString("buyer_last").toUpperCase();
                    Iterator<String> iterator = json.keys();
                    while (iterator.hasNext()){
                        String key = iterator.next();
                        if (json.get(key) instanceof String){
                            if (json.getString(key).toUpperCase().indexOf(str) != -1) {
                                tmp.add(json);
                            }
                        }else if (key.equals("custom_fields")){
                            JSONArray arr = json.getJSONArray("custom_fields");
                            boolean include = false;
                            for (int j=0;j<arr.length(); j++){
                                JSONArray one = arr.getJSONArray(j);
                                if (one.length()>0) {
                                    for (int k=0;k<one.length();k++){
                                        if (one.get(k) instanceof String){
                                            if (((String) one.get(k)).toUpperCase().indexOf(str) >= 0 ){
                                                include = true;
                                                continue;
                                            }
                                        }
                                    }
                                }
                                if (include) continue;
                            }
                            if (include) {
                                tmp.add(json);
                            }
                        }

                    }
                } catch (JSONException jex) {
                    jex.printStackTrace();
                }
            }
        }
        //

        //arrayList.clear();
        //localSearchSize = tmp.size();
        currList.clear();
        if (!str.equals(""))
            currList.addAll(tmp);
        else
            currList.addAll(initialList);

        //ApiCall ac = new ApiCall(urlpart + Integer.toString(page) + "/" + str, List.this, 1);
        adapter.notifyDataSetChanged();
        //hideKeyboard();
    }
    private String urlpart = "";
    private boolean loadingAdditionl = false;
    private int localSearchSize = 0;

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private boolean noMore = false;
    private int getListCount = 0;
    private ArrayList<JSONObject> initialList = new ArrayList<>();

    @Override
    public void call(String result, boolean success, int id) { // 7 for details 1 for main list 8 for check_in from popup
        if (id==1) {
            loadingAdditionl = false;
            Globals.cancelProgressDialog();
            if (success) {
                try {
                    arrayList.clear();
                    JSONArray arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        if (obj.has("data")) {
                            JSONObject o = obj.getJSONObject("data");
                            arrayList.add(o);
                        }
                        if (obj.has("additional") && !Globals.show_at_once) {
                            Log.d("ADDITIONAL", Integer.toString(obj.getJSONObject("additional").getInt("results_count")));
                            if (obj.getJSONObject("additional").getInt("results_count") > 0) {
                                page++;
                                //ApiCall ac = new ApiCall(urlpart+Integer.toString(page), this, 1);
                            } else {
                                noMore = true;
                            }
                        }else {
                            noMore = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (getListCount == 0){
                    initialList.addAll(arrayList);
                }
                getListCount++;
                //if (noMore) {
                //currList.clear();
                boolean alreadyThere;
                if (arrayList.size()>0) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        alreadyThere = false;
                        for (int j = 0; j < localSearchSize && !alreadyThere; j++) {
                            try {
                                if (arrayList.get(i).getString("transaction_id").equals(currList.get(j).getString("transaction_id"))) {
                                    alreadyThere = true;
                                }
                            } catch (JSONException jex) {
                                jex.printStackTrace();
                            }
                        }
                        if (!alreadyThere)
                            currList.add(arrayList.get(i));
                    }
                }

                if (currList.size() > initialList.size()) {
                    initialList.clear();
                    initialList.addAll(currList);
                }

                localSearchSize = -1;
                adapter.notifyDataSetChanged();

                if (!noMore){
                    ApiCall ac = new ApiCall(urlpart + Integer.toString(page), this, 1);
                }
                //}
            }
        }else if (id==7){
            populateCheckins(result, success);
        }else if(id==8){
            Globals.cancelProgressDialog();
            if (success) {
                try {
                    JSONObject job = new JSONObject(result);
                    if (job.has("checksum") && job.has("status") && job.has("pass")) {
                        if (job.getBoolean("status") && job.getBoolean("pass")) {
                            if (Globals.show_attendee_screen)
                                populatePopup2(job);
                            Globals.showSuccess(this.getActivity(), Globals.show_attendee_screen);
                        }else{
                            Globals.showFailCheck(this.getActivity());
                        }
                    }
                }catch (JSONException jex){
                    if (result.lastIndexOf("Ticket does not exist")!=-1){
                        Globals.showFailCheck(this.getActivity());
                    }
                    jex.printStackTrace();
                }
            }else{
                Globals.showFailCheck(this.getActivity());
            }
        }
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

    private void translate(){
        TextView idt = (TextView)view.findViewById(R.id.id_text);
        TextView purcht = (TextView)view.findViewById(R.id.purchased_textt);
        TextView chknins = (TextView)view.findViewById(R.id.checkins_text);
        TextView chkin = (TextView)view.findViewById(R.id.checkin_text);
        idt.setText(Globals.getTranslation("ID")+":");
        purcht.setText(Globals.getTranslation("PURCHASED")+":");
        chknins.setText(Globals.getTranslation("CHECKINS"));
        chkin.setText(Globals.getTranslation("CHECK_IN"));
    }


}
