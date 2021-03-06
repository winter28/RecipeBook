package ddwu.mobile.finalproject.ma01_20180988;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class RecipeDetailActivity extends AppCompatActivity {
    private static final String TAG = "RecipeDetailActivity";

    ImageView ivDetailRcpImg;
    TextView tvDetailRcpName, tvDetailStep;
    ListView lvDetailIngre;
    ViewPager vpManual;

    ArrayAdapter ingredientAdapter;
    ManualAdapter manualAdapter;
    NetworkManager networkManager;
    Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        ivDetailRcpImg = findViewById(R.id.ivDetailRcpImg);
        tvDetailRcpName = findViewById(R.id.tvDetailRcpName);
        lvDetailIngre = findViewById(R.id.lvDetailIngre);
        vpManual = findViewById(R.id.vpManual);
        tvDetailStep = findViewById(R.id.tvDetailStep);

        ingredientAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, recipe.getIngredients());
        manualAdapter = new ManualAdapter(this, recipe.getManuals());
        networkManager = new NetworkManager(this);

        lvDetailIngre.setAdapter(ingredientAdapter);
        tvDetailRcpName.setText(recipe.getName());
        vpManual.setAdapter(manualAdapter);
        vpManual.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tvDetailStep.setText(String.valueOf(recipe.getManuals().get(position).getStep()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        new GetImageAsyncTask().execute(recipe.getImageLink());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        RecipeDBManager manager = new RecipeDBManager(this);
        if (manager.addNewRecipe(recipe)) {
            Toast.makeText(this, "내 레시피에 저장 완료!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "내 레시피에 저장 실패!", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
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
                ivDetailRcpImg.setImageBitmap(bitmap);
            }
        }
    }
}
