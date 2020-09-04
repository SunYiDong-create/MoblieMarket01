package MoblieMarket.com.MoblieMarket;

import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;

import MoblieMarket.com.MoblieMarket.bean.User;


public class BaseActivity extends AppCompatActivity {



    public void startActivity(Intent intent,boolean isNeedLogin){


        if(isNeedLogin){

            User user = MoblieMarket.getInstance().getUser();
            if(user !=null){
                super.startActivity(intent);
            }
            else{

                MoblieMarket.getInstance().putIntent(intent);
                Intent loginIntent = new Intent(this
                        , LoginActivity.class);
                super.startActivity(intent);

            }

        }
        else{
            super.startActivity(intent);
        }

    }
}
