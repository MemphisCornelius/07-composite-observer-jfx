package ohm.softa.a07.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import ohm.softa.a07.api.OpenMensaAPI;
import ohm.softa.a07.model.Meal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	private static final Logger logger = LogManager.getLogger(MainController.class);

	// use annotation to tie to component in XML
	@FXML
	private Button btnRefresh;

	@FXML
	private Button btnClose;

	@FXML
	private CheckBox chkVegetarian;

	@FXML
	private ListView<String> mealsList;

	private OpenMensaAPI openMensaAPI;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// set the event handler (callback)
		btnRefresh.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				// create a new (observable) list and tie it to the view
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				String today = sdf.format(new Date());

				Call<List<Meal>> call = openMensaAPI.getMeals(today);
				call.enqueue(new Callback<List<Meal>>() {
					@Override
					public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
						if (response.isSuccessful()) {
							List<Meal> meals = response.body();

							ObservableList<String> list = FXCollections.observableArrayList();
							meals.forEach(m -> {
								if (!chkVegetarian.isSelected() || m.isVegeterian()) {
									list.add(m.toString());
								}

							});
							mealsList.setItems(list);
						}
					}

					@Override
					public void onFailure(Call<List<Meal>> call, Throwable t) {
						logger.info("Call was unsuccessful");
					}
				});


			}
		});

		chkVegetarian.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				logger.info("vegeterian Checkbox is " + event.toString());
			}
		});

		btnClose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		Retrofit retrofit = new Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl("https://openmensa.org/api/v2/")
			.build();

		openMensaAPI = retrofit.create(OpenMensaAPI.class);
	}
}
