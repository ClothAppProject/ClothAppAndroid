package Resources;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by giacomoceribelli on 02/01/16.
 */
public class ParseApplication extends Application {
//Questa classe viene chiamata ogni ciclo di vita intero dell'applicazione, infatti parse va invocato solamente una volta durante tutto
//il ciclo di vita dell'app mentre se si lasciava nella launcher se la launcher era richiamata andava in errore perchè si cercava
//di inizializzare parse un'altra volta.
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
    }
}