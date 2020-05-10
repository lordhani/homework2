package com.example.hw2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link frag_news#newInstance} factory method to
 * create an instance of this fragment.
 */
public class frag_news extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private static String URL = "https://www.aybu.edu.tr/muhendislik/bilgisayar/";
    private ProgressDialog progressDialog;
    private int destroyDuplicates = 0;
    String direct;

    public ArrayList newsList = new ArrayList();
    public ArrayList hrefList = new ArrayList();

    public frag_news() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment frag_food.
     */
    // TODO: Rename and change types and number of parameters
    public static frag_news newInstance(String param1, String param2) {
        frag_news fragment = new frag_news();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_frag_news, container, false);
        listView = (ListView) rootView.findViewById(R.id.newsList);

        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,newsList);

        new frag_news.FetchData().execute();
        addClickListener();

        return rootView;
    }

    private void addClickListener()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < newsList.size(); i++)
                {
                    if(newsList.get(position) == newsList.get(i))
                    {
                        direct = URL + hrefList.get(i);
                    }
                }
                Object currentItem = newsList.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(direct));
                startActivity(intent);
            }
        });
    }

    private class FetchData extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Lütfen Bekleyiniz.");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                Document doc = Jsoup.connect(URL).timeout(30*1000).get();
                Elements news = doc.select ("a[title]");
                String newsId = "ContentPlaceHolder1_ctl01_rpData_hplink_";

                int anncounter = 0;
                String comparison;

                for (int i = 0; i < news.size(); i++)
                {
                    if(destroyDuplicates != 0) break;
                    comparison = newsId + Integer.toString(anncounter);
                    if(news.get(i).id().equals(comparison))
                    {
                        newsList.add(news.get(i).text());
                        hrefList.add(news.get(i).attr("href"));
                        anncounter++;
                    }
                }
                destroyDuplicates++;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);

            listView.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }
}
