package ddwu.mobile.finalproject.ma01_20180988;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MyRecipeActivity extends AppCompatActivity {
    private static final String TAG = "MyRecipeActivity";
    private ListView lvMyRecipe;
    private RecipeAdapter recipeAdapter;
    private ArrayList<Recipe> recipeList;
    private RecipeDBManager manager;
    private FloatingActionButton fabNewRecipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipe);

        manager = new RecipeDBManager(this);
        recipeList = new ArrayList<>();
        fabNewRecipe = findViewById(R.id.fabNewRecipe);
        lvMyRecipe = findViewById(R.id.lvMyRecipe);
        recipeAdapter = new RecipeAdapter(this, R.layout.recipe_adapter_view, recipeList);
        lvMyRecipe.setAdapter(recipeAdapter);

        lvMyRecipe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyRecipeActivity.this, MyRecipeDetailActivity.class);
                intent.putExtra("recipe", recipeList.get(position));
                startActivity(intent);
            }
        });

        fabNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyRecipeActivity.this, NewRecipeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recipeList.clear();
        recipeList.addAll(manager.getMyRecipe());
        recipeAdapter.setList(recipeList);
    }
}
