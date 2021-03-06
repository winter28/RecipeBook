package ddwu.mobile.finalproject.ma01_20180988;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Random;

public class FragmentHome extends Fragment {
    private NetworkManager networkManager;
    private RecipeXmlParser parser;
    private Recipe recipe;

    private TextView tvRecommRCP;
    private ImageView ivRecommRCP;
    private Button btnMyRecipe;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        networkManager = new NetworkManager(getContext());
        parser = new RecipeXmlParser();

        tvRecommRCP = view.findViewById(R.id.tvRecommRCP);
        ivRecommRCP = view.findViewById(R.id.ivRecommRCP);
        btnMyRecipe = view.findViewById(R.id.btnMyRecipe);

        Random rand = new Random();
        int recipeNum = rand.nextInt(1236) + 1;

        new NetworkAsyncTask().execute(getString(R.string.recommend_recipe_api_url ) + recipeNum + "/" + recipeNum);

        tvRecommRCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recipe != null) {
                    Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
                    intent.putExtra("recipe", recipe);
                    startActivity(intent);
                }
            }
        });

        ivRecommRCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recipe != null) {
                    Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
                    intent.putExtra("recipe", recipe);
                    startActivity(intent);
                }
            }
        });

        btnMyRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MyRecipeActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    class NetworkAsyncTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDlg = new ProgressDialog(getContext(), R.style.AppCompatAlertDialogStyle);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg.setTitle("Wait");
            progressDlg.setMessage("Loading...");
            progressDlg.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result;

            result = networkManager.downloadContents(address);
            if (result == null) return "Error";

            recipe = parser.parse(result).get(0);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            tvRecommRCP.setText(recipe.getName());

            new GetImageAsyncTask().execute(recipe.getImageLink());

            progressDlg.dismiss();
        }
    }

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        RecipeAdapter.ViewHolder viewHolder;
        String imageAddress;

        public GetImageAsyncTask() {
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            imageAddress = params[0];
            Bitmap result;
            result = networkManager.downloadImage(imageAddress);
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                ivRecommRCP.setImageBitmap(bitmap);
            }
        }
    }
}