package de.smart_efb.efbapp.smartefb;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by ich on 24.05.16.
 */
public class mainMenueOnClickListener implements View.OnClickListener {


    int buttonNumber;
    Context context;

    public mainMenueOnClickListener (Context context ,int buttonNumber) {
        this.buttonNumber = buttonNumber;
        this.context = context;
    }


    @Override
    public void onClick(View v) {

        Intent intent;

        switch (buttonNumber) {

            case 0:
                // its a textfield -> no action
                break;
            case 1: // Button "Connect book"
                intent = new Intent(context, ActivityConnectBook.class);
                context.startActivity(intent);
                break;
            case 2: // Button "our arrangement"
                intent = new Intent(context, ActivityOurArrangement.class);
                context.startActivity(intent);
                break;
            case 3: // Button "my+your goals"
                Toast.makeText(context," Meine + Deine Ziele ", Toast.LENGTH_SHORT).show();
                //intent = new Intent(context, .class);
                //context.startActivity(intent);
                break;
            case 4: // Button "prevention"
                Toast.makeText(context," Praevention ", Toast.LENGTH_SHORT).show();
                //intent = new Intent(context, .class);
                //context.startActivity(intent);
                break;
            case 5: // Button "na.n"
                //intent = new Intent(context, .class);
                //context.startActivity(intent);
                break;
            case 6: // Button "nb.n"
                //intent = new Intent(context, .class);
                //context.startActivity(intent);
                break;
            case 7:
                // its a textfield -> no action
                break;
            case 8: // Button "faq"
                //intent = new Intent(context, .class);
                //context.startActivity(intent);
                break;
            case 9:
                // its a textfield -> no action
                break;
            case 10: // Button "make meeting"
                //intent = new Intent(context, .class);
                //context.startActivity(intent);
                break;
            case 11: // Button "emergency help"
                //intent = new Intent(context, .class);
                //context.startActivity(intent);
                break;
            case 12:
                // its a textfield -> no action
                break;
            default:
                break;

        }

    }
}
