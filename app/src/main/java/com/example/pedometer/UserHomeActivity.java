package com.example.pedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    CardView c1,c2,c3,c4,c5,c6;
    ImageView imgbmi,imggyrro,imgwater,imgjog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        c1=findViewById(R.id.cardsteps);
        c2=findViewById(R.id.cardrunning);
        c3=findViewById(R.id.cardbmi);
        imgbmi=findViewById(R.id.imagebmi);
        imggyrro=findViewById(R.id.imagegyro);
        imgwater=findViewById(R.id.water);
        imgjog=findViewById(R.id.imgjog);


        c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(UserHomeActivity.this,StepscountActivity.class);
                startActivity(intent);

            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHomeActivity.this,RunningActivity.class);
                startActivity(intent);

            }
        });
        imgbmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHomeActivity.this,BMIActivity.class);
                startActivity(intent);
            }
        });

        imggyrro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHomeActivity.this,GyrosensorActivity.class);
                startActivity(intent);
            }
        });
        imgwater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHomeActivity.this,FoodcalActivity.class);
                startActivity(intent);
            }
        });
        imgjog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserHomeActivity.this,JoggingActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_ContactUs:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ContactUsFragment()).commit();
                break;

            case R.id.nav_feedback:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FeedbackFragment()).commit();
                break;

            case R.id.nav_share:
                Intent emailIntent = new Intent();
                emailIntent.setAction(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_text)+getApplicationContext().getPackageName());
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                emailIntent.setType("message/rfc822");

                PackageManager pm = getPackageManager();
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("text/plain");

                ApplicationInfo app = getApplicationContext().getApplicationInfo();
                String filePath = app.sourceDir;

                Intent openInChooser = Intent.createChooser(emailIntent,"Share via:");

                List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
                List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
                for (int i = 0; i < resInfo.size(); i++)
                {
                    // Extract the label, append it, and repackage it in a LabeledIntent
                    ResolveInfo ri = resInfo.get(i);
                    String packageName = ri.activityInfo.packageName;
                    if(packageName.contains("android.email"))
                    {
                        emailIntent.setPackage(packageName);
                    }
                    else if( packageName.contains("anyshare") ||  packageName.contains("android.bluetooth")
                            || packageName.contains("hangouts") || packageName.contains("hike") ||
                            packageName.contains("twitter") || packageName.contains("facebook") ||
                            packageName.contains("mms") || packageName.contains("android.gm") ||
                            packageName.contains("whatsapp"))
                    {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT,  getString(R.string.sharing_text)+getApplicationContext().getPackageName());

                        if(packageName.contains("android.gm"))
                        {
                            intent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
                            intent.setType("message/rfc822");
                        }
                        if ( (packageName.contains("android.bluetooth") || packageName.contains("anyshare")) && filePath!=null )
                        {
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
                            intent.setType("*/*");
                        }
                        intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                    }
                }

                // convert intentList to array
                LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

                openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
                startActivity(openInChooser);

                break;

            case R.id.nav_rate:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName()));
                startActivity(intent);
                break;

            case R.id.nav_Logout:
              buildAlert();
                break;

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void buildAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure! do you want to Sign out?")
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(UserHomeActivity.this,LoginActivity.class));
                        finish();
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
