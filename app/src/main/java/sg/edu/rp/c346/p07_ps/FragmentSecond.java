package sg.edu.rp.c346.p07_ps;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class FragmentSecond extends Fragment {

    Button btnRetrieve2, btnEmail;
    TextView tvFrag2, tvOutput2;
    EditText et2;

    String smsBody = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        tvFrag2 = view.findViewById(R.id.tvFrag2);
        btnRetrieve2 = view.findViewById(R.id.btnRetrieve2);
        tvOutput2 = view.findViewById(R.id.tvOutput2);
        btnEmail = view.findViewById(R.id.btnEmail);
        et2 = view.findViewById(R.id.etWord);


        btnRetrieve2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Uri uri = Uri.parse("content://sms");

                String userWord = et2.getText().toString();
                String[] splitStr = userWord.trim().split("\\s+");

                String[] reqCols = new String[]{"date", "address", "body", "type"};
                ContentResolver cr = getActivity().getApplicationContext().getContentResolver();

                for(int i =0; i<splitStr.length; i++) {
                    String filter = "body LIKE ?";
                    String[] filterArgs = {"%" + splitStr[i] + "%"};

                    Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);

                    if (cursor.moveToFirst()) {
                        do {
                            long dateInMillis = cursor.getLong(0);
                            String date = (String) DateFormat
                                    .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                            String address = cursor.getString(1);
                            String body = cursor.getString(2);
                            String type = cursor.getString(3);
                            if (type.equalsIgnoreCase("1")) {
                                type = "Inbox:";
                            } else {
                                type = "Sent:";
                            }
                            smsBody += type + " " + address + "\n at " + date
                                    + "\n\"" + body + "\"\n\n";
                        } while (cursor.moveToNext());
                    }
                }
                tvOutput2.setText(smsBody);
            }
        });

        btnEmail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"17053109@myrp.edu.sg"});
                email.putExtra(Intent.EXTRA_SUBJECT, "SMS");
                email.putExtra(Intent.EXTRA_TEXT, smsBody);

                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
            }
        });

        return view;
    }

}
