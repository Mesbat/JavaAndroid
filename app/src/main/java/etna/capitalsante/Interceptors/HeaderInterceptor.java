package etna.capitalsante.Interceptors;

import android.content.Context;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Interceptor;
import okhttp3.Request;

public class HeaderInterceptor
        implements Interceptor {
    private Context context;
    public HeaderInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public okhttp3.Response intercept(Chain chain)
            throws IOException {
        HashSet<String> cookies = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context).getStringSet("PREF_COOKIES", new HashSet<String>());
        String str = String.valueOf(cookies);
        Pattern pattern = Pattern.compile("connect.sid=(.*?);");
        Matcher matcher = pattern.matcher(str);
        matcher.find();
        Request request = chain.request();
        request = request.newBuilder()
                .addHeader("app", "Capital Sante")
                .addHeader("Cookie", "connect.sid=" + matcher.group(1))
                .addHeader("User-Agent", System.getProperty("http.agent"))
                .build();
        okhttp3.Response response = chain.proceed(request);
        return response;
    }
}