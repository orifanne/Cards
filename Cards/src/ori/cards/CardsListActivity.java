package ori.cards;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class CardsListActivity extends Activity {

	// колода карт
	static ArrayList<Card> cards;

	// для отображения списка вытащенных карт
	private ListView list;
	// адептер, работающий со списком
	private SimpleAdapter adapter;
	// список хэш-карт (название, код изображения),
	// для отображения вытащенных из колоды карт
	private ArrayList<HashMap<String, Integer>> cardsList;
	static final String IMGKEY = "key";

	// диалоги
	AlertDialog alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cards_list);

		// делаем саму колоду
		cards = new ArrayList<Card>();

		cardsList = new ArrayList<HashMap<String, Integer>>();
		setUpCards();

		// Log.d("*****", Integer.toString(cardsList.size()));

		// делаем адаптер и связываем его со списком для отображения
		adapter = new SimpleAdapter(this, cardsList, R.layout.item,
				new String[] { IMGKEY }, new int[] { R.id.img });
		list = (ListView) findViewById(R.id.list_all_cards);
		list.setAdapter(adapter);
		// и вешаем на него слушатель нажатий
		list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				show(cards.get(position));
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cards_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_instructions:
			if (alert != null)
				alert.cancel();
			alert = DialogScreen._getDialog(this, DialogScreen.ABOUT, null);
			alert.show();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	// показать карту
	void show(Card card) {

		if (card == null)
			return;
		if (alert != null)
			alert.cancel();
		alert = DialogScreen._getDialog(this, DialogScreen.CARDINFO, card);
		alert.show();
	}

	// сделать колоду карт
	private void setUpCards() {
		HashMap<String, Integer> hm;
		Card c;
		hm = new HashMap<String, Integer>();
		c = new Card("1", "red", R.drawable.r1, R.drawable.r1s,
				"Личность, человек, интроспекция, существо");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("2", "red", R.drawable.r2, R.drawable.r2s,
				"Преграда, отграничивание, защита");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("3", "red", R.drawable.r3, R.drawable.r3s,
				"Действие, реализация, творчество, форма, образ");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("4", "red", R.drawable.r4, R.drawable.r4s,
				"Воля, эмоции, интенсивность");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("5", "red", R.drawable.r5, R.drawable.r5s,
				"Встреча, пара, отношение");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("6", "red", R.drawable.r6, R.drawable.r6s,
				"Владение, собственность");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("7", "red", R.drawable.r7, R.drawable.r7s,
				"Прошлое, связность, причина-следствие, +маятник, интуиция");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("8", "red", R.drawable.r8, R.drawable.r8s,
				"Дорога, путь, движение, развитие, изменение");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("9", "red", R.drawable.r9, R.drawable.r9s,
				"Стихия, мир, жизнь, сродство");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("10", "red", R.drawable.r10, R.drawable.r10s,
				"Закон, уклад, естественность, совершенство");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("13", "red", R.drawable.r13, R.drawable.r13s,
				"Рассвет, изменение, перемены, выход из контекста");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("17", "red", R.drawable.r17, R.drawable.r17s,
				"Ткань творения, сила, сейчас");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("1", "black", R.drawable.b1, R.drawable.b1s,
				"Сомнение, слабость, неуверенность, отчаяние");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("2", "black", R.drawable.b2, R.drawable.b2s,
				"Страх, неприятие, замкнутость?");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("3", "black", R.drawable.b3, R.drawable.b3s,
				"Маска, ложь, игра");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("4", "black", R.drawable.b4, R.drawable.b4s,
				"Поток, подверженность, подавление воли, контроль, вовлеченность");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("5", "black", R.drawable.b5, R.drawable.b5s,
				"Поединок, противоречие, две воли, противостояние, боль");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("6", "black", R.drawable.b6, R.drawable.b6s,
				"Разделение, безразличие, различение, общность");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("7", "black", R.drawable.b7, R.drawable.b7s,
				"Неожиданность, неучтённое, случай, +туман, сокрытое");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("8", "black", R.drawable.b8, R.drawable.b8s,
				"Перекрёсток, выбор, альтернатива, равнозначность");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("9", "black", R.drawable.b9, R.drawable.b9s,
				"Бездна мира, страсть, тьма");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("10", "black", R.drawable.b10, R.drawable.b10s,
				"Лезвие, напряжение, разрушение");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("13", "black", R.drawable.b13, R.drawable.b13s,
				"Статика, смерть");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("17", "black", R.drawable.b17, R.drawable.b17s,
				"Холод, небытие, время");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);

		hm = new HashMap<String, Integer>();
		c = new Card("0", "", R.drawable.zero, R.drawable.zeros,
				"Творение, свобода, новая судьба");
		cards.add(c);
		hm.put(IMGKEY, c.getSPicture());
		cardsList.add(hm);
	}
}
