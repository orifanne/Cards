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
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class MainActivity extends Activity {

	// колода карт
	static ArrayList<Card> cards;
	// карты, вытащенные из колоды
	static ArrayList<Card> chousenCards;
	// карты, отложенные в сторону
	static ArrayList<Boolean> savedCards;

	// карта дня
	Card dayCard = null;
	// карта, которую нужно показывать (запоминается для диалога)
	Card cardShow = null;
	// наличие выбранной карты дня
	public boolean hasDayCard = false;

	// для отображения списка вытащенных карт
	private ListView list;
	// адептер, работающий со списком
	private SimpleAdapter adapter;
	// список хэш-карт (название, код изображения),
	// для отображения вытащенных из колоды карт
	private ArrayList<HashMap<String, Integer>> cardsList;
	static final String IMGKEY = "key";

	// рандомайзер для вытаскивания карт
	private Random rand;
	// режим вытаскивания карт (рубашкой вниз или вверх)
	private boolean mode = true;

	// изображение колоды
	private ImageView im;

	// таймер на длинное нажатие
	Timer timer = new Timer();
	// диалоги
	AlertDialog alert;

	// флаг того, что еще можно вытаскивать карты (влезают в экран)
	boolean canTake = true;
	// количество карт, которые можно вытащить (в зависимости от размера экрана)
	int cardsNum;

	private static int k = 0;
	private static int h = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// рисуем изображение колоды и вешаем на него слушатель нажатий
		im = (ImageView) findViewById(R.id.backImage);
		im.setImageResource(R.drawable.back2);
		im.setOnTouchListener(new cardsTouchListener());

		// делаем саму колоду
		cards = new ArrayList<Card>();
		setUpCards();
		// и инициализируем все списки
		chousenCards = new ArrayList<Card>();
		savedCards = new ArrayList<Boolean>();
		cardsList = new ArrayList<HashMap<String, Integer>>();

		// инициализируем рандомайзер
		rand = new Random(new Date().getTime());

		// делаем адаптер и связываем его со списком для отображения
		adapter = new SimpleAdapter(this, cardsList, R.layout.item,
				new String[] { IMGKEY }, new int[] { R.id.img });
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(adapter);
		// и вешаем на него слушатель нажатий
		list.setOnTouchListener(new chousenCardsTouchListener());

		// считаем количество карт, которые можно вытаскивать
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y
				- BitmapFactory.decodeResource(this.getResources(),
						R.drawable.back2).getHeight()
				- BitmapFactory.decodeResource(this.getResources(),
						R.drawable.b1).getHeight();
		cardsNum = height
				/ BitmapFactory.decodeResource(this.getResources(),
						R.drawable.b1s).getHeight()
				+ ((BitmapFactory.decodeResource(this.getResources(),
						R.drawable.b1).getHeight() / 3) * 2)
				/ BitmapFactory.decodeResource(this.getResources(),
						R.drawable.b1s).getHeight() + 1;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_instructions:
			if (alert != null)
				alert.cancel();
			alert = DialogScreen.getDialog(this, DialogScreen.ABOUT, null);
			alert.show();
			break;
		case R.id.action_cadrs_list:
			Intent i = new Intent(this, CardsListActivity.class);
			startActivity(i);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	// сменить режим вытаскивания карт (вверх/вниз рубашкой)
	public void changeMode() {
		mode = !mode;
		if (mode)
			im.setImageResource(R.drawable.back2);
		else
			im.setImageResource(R.drawable.back1);
	}

	// вытащить карту из колоды
	public void take() {
		if ((cards.size() <= 0) || (!canTake))
			return;
		int num = 0;
		if (cards.size() > 1)
			num = rand.nextInt(cards.size() - 1);

		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		if (mode)
			hm.put(IMGKEY, cards.get(num).getPicture());
		else
			hm.put(IMGKEY, R.drawable.back);

		if (chousenCards.size() > 0) {
			HashMap<String, Integer> hm1 = new HashMap<String, Integer>();
			if (cardsList.get(chousenCards.size() - 1).get(IMGKEY) == R.drawable.back)
				hm1.put(IMGKEY, R.drawable.backs);
			else
				hm1.put(IMGKEY, chousenCards.get(chousenCards.size() - 1)
						.getSPicture());
			cardsList.remove(chousenCards.size() - 1);
			cardsList.add(hm1);
		}

		cardsList.add(hm);
		chousenCards.add(cards.get(num));
		savedCards.add(false);
		cards.remove(num);
		list.invalidateViews();
		adapter.notifyDataSetChanged();
		refreshPaddings();

		// контролируем количество вытащенных карт
		if (chousenCards.size() == cardsNum)
			canTake = false;
	}

	// вернуть все карты в колоду
	private void ret() {
		if (chousenCards.size() == 0)
			return;
		// определяем, есть ли у нас несохраненные карты
		boolean f = false;
		for (int i = chousenCards.size() - 1; i >= 0; i--)
			if (!savedCards.get(i))
				f = true;
		// если несохраненных карт нет,
		// возвращаем все сохраненные
		if (!f) {
			for (int i = chousenCards.size() - 1; i >= 0; i--) {
				Card c = chousenCards.get(i);
				cardsList.remove(i);
				chousenCards.remove(i);
				savedCards.remove(i);
				cards.add(c);
			}
		}
		// иначе возвращаем только несохраненные
		else {
			for (int i = chousenCards.size() - 1; i >= 0; i--) {
				if (!savedCards.get(i)) {
					Card c = chousenCards.get(i);
					cardsList.remove(i);
					chousenCards.remove(i);
					savedCards.remove(i);
					cards.add(c);
				} else
					unsaveCard(i);
			}

			if (chousenCards.size() != 0) {

				for (int i = chousenCards.size() - 1; i >= 0; i--) {
					saveCard(i);
				}

				HashMap<String, Integer> hm = null;
				hm = new HashMap<String, Integer>();
				if (cardsList.get(cardsList.size() - 1).get(IMGKEY) == R.drawable.backs) {
					hm.put(IMGKEY, R.drawable.back);
				} else {
					hm.put(IMGKEY, chousenCards.get(cardsList.size() - 1)
							.getPicture());
				}
				cardsList.remove(cardsList.size() - 1);
				cardsList.add(hm);
			}
		}

		adapter.notifyDataSetChanged();
		refreshPaddings();

		// контролируем количество вытащенных карт
		if (chousenCards.size() < cardsNum)
			canTake = true;
	}

	// веруть выбранную карту в колоду
	private void ret(int i) {
		if ((i > (chousenCards.size() - 1)) || (i < 0))
			return;
		HashMap<String, Integer> hm = null;
		if ((i == (chousenCards.size() - 1)) && (i > 0)) {
			hm = new HashMap<String, Integer>();
			if (cardsList.get(i - 1).get(IMGKEY) == R.drawable.backs)
				hm.put(IMGKEY, R.drawable.back);
			else
				hm.put(IMGKEY, chousenCards.get(i - 1).getPicture());
		}
		Card c = chousenCards.get(i);

		cardsList.remove(i);
		chousenCards.remove(i);
		savedCards.remove(i);
		if ((i == chousenCards.size()) && (i > 0)) {
			cardsList.remove(i - 1);
			cardsList.add(hm);
		}
		cards.add(c);
		adapter.notifyDataSetChanged();
		refreshPaddings();

		// контролируем количество вытащенных карт
		if (chousenCards.size() < cardsNum)
			canTake = true;
	}

	// перевернуть карту
	private void flip(int i) {
		if ((i > (chousenCards.size() - 1)) || (i < 0))
			return;
		HashMap<String, Integer> hm = cardsList.get(i);
		if (i == (chousenCards.size() - 1)) {
			if (hm.get(IMGKEY) == R.drawable.back)
				hm.put(IMGKEY, chousenCards.get(i).getPicture());
			else
				hm.put(IMGKEY, R.drawable.back);
		} else {
			if (hm.get(IMGKEY) == R.drawable.backs)
				hm.put(IMGKEY, chousenCards.get(i).getSPicture());
			else
				hm.put(IMGKEY, R.drawable.backs);
		}

		adapter.notifyDataSetChanged();
	}

	private void refreshPaddings() {
		if (chousenCards.size() == 0)
			return;
		for (int i = 0; i < chousenCards.size(); i++) {
			View v1 = list.getChildAt(i);
			if (v1 != null) {
				System.out.println(i + " " + savedCards.get(i));
				if (savedCards.get(i))
					v1.setPadding(0, 0, 200, 0);
				else
					v1.setPadding(0, 0, 0, 0);
			}
		}
	}

	// выбрать карту дня
	void chooseDayCard() {
		if (alert != null)
			alert.cancel();
		alert = DialogScreen.getDialog(this, DialogScreen.DAYCARD, dayCard);
		alert.show();
	}

	// вытащить карту дня (вызывается из диалога)
	public void takeDayCard() {
		if (cards.size() <= 0)
			return;
		int num = 0;
		if (cards.size() > 1)
			num = rand.nextInt(cards.size() - 1);
		dayCard = cards.get(num);
		cards.remove(num);
		hasDayCard = true;
	}

	// вернуть карту дня в колоду
	public void retDayCard() {
		if (dayCard == null)
			return;
		cards.add(dayCard);
		dayCard = null;
		hasDayCard = false;
	}

	// отложить карту в сторону
	private void saveCard(int i) {
		if ((i > (chousenCards.size() - 1)) || (i < 0))
			return;
		savedCards.set(i, true);
		refreshPaddings();
	}

	// вернуть карту обратно в общий список выбранных
	private void unsaveCard(int i) {
		if ((i > (chousenCards.size() - 1)) || (i < 0))
			return;
		savedCards.set(i, false);
		refreshPaddings();
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

	// слушатель нажатий на колоду
	public class cardsTouchListener implements OnTouchListener {
		// флаг перетаскивания
		private boolean f = false;
		// для запоминания предыдущих координат
		private float prevY;
		private float prevX;
		// текущее время (запоминается для таймера длинного нажатия)
		long ms;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// фиксируем координаты касания
			float y = event.getY();
			float x = event.getX();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: // нажатие
				// запоминаем, где было нажатие
				prevY = y;
				prevX = x;
				// запоминаем время для таймера длинного нажатия
				ms = new Date().getTime();
				// включаем флаг перетаскивания
				f = true;

				// возможно, нам понадобится выбирать карту дня,
				// если это будет длинное нажатие
				ChooseDayCardTask task = new ChooseDayCardTask();
				timer = new Timer();
				timer.schedule(task, 333);

				break;
			case MotionEvent.ACTION_MOVE: // перемещение
				// если тапают вниз, вытаскиваем карту
				if (((y - prevY) > v.getHeight() / 4) && f) {
					take();
					f = false;
					timer.cancel();
				}
				// если тапают вверх, убираем все карты
				// (сперва все несохраненные, если несохраненных нет,
				// то убираем и сохраненные)
				if (((prevY - y) > v.getHeight() / 4) && f) {
					ret();
					f = false;
					timer.cancel();
				}
				// если тапают вправо, ничего не делаем
				if (((x - prevX) > v.getWidth() / 6) && f) {
					f = false;
					timer.cancel();
					h++;
					if (h == 10) {
						Toast toast = Toast.makeText(getApplicationContext(),
								"Ok, you fuckin' nerd got it.\n\n"
										+ "______( · Y ·)______\n",
								Toast.LENGTH_SHORT);
						toast.show();
						h = 0;
					}
				}
				// если тапают влево, ничего не делаем
				if (((prevX - x) > v.getWidth() / 6) && f) {
					f = false;
					timer.cancel();
					k++;
					if (k == 2) {
						Toast toast = Toast.makeText(getApplicationContext(),
								"There are no Easter Eggs in this program.",
								Toast.LENGTH_SHORT);
						toast.show();
					}
					if (k == 4) {
						Toast toast = Toast
								.makeText(
										getApplicationContext(),
										"There really are no Easter Eggs in this program.",
										Toast.LENGTH_SHORT);
						toast.show();
					}
					if (k == 6) {
						Toast toast = Toast
								.makeText(
										getApplicationContext(),
										"Didn't I already tell you that there are no Easter Eggs in this program?",
										Toast.LENGTH_SHORT);
						toast.show();
					}
					if (k == 8) {
						Toast toast = Toast.makeText(getApplicationContext(),
								"All right, you win.\n", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						LinearLayout toastContainer = (LinearLayout) toast
								.getView();
						ImageView catImageView = new ImageView(
								getApplicationContext());
						catImageView.setImageResource(R.drawable.omg);
						toastContainer.addView(catImageView, 0);
						toast.show();
					}
					if (k == 10) {
						Toast toast = Toast
								.makeText(
										getApplicationContext(),
										"What is it?  It's an elephant being eaten by a snake, of course.",
										Toast.LENGTH_SHORT);
						toast.show();
						k = 0;
					}
				}
				break;
			case MotionEvent.ACTION_UP: // отпускание
				// если это не длинное нажатие
				// (а если длинное, там сработает асинхронное задание)
				if ((new Date().getTime() - ms) < 333) {
					f = false;
					timer.cancel();
					// если не было тапа, меняем режим
					if ((Math.abs(x - prevX) < v.getWidth() * 0.01)
							&& (Math.abs(y - prevY) < v.getHeight() * 0.01))
						changeMode();
				}
				break;
			}
			return true;
		}
	}

	// слушатель нажатий на выбранные карты
	public class chousenCardsTouchListener implements OnTouchListener {
		// флаг перетаскивания
		private boolean f = false;
		// для запоминания предыдущих координат
		private float prevY;
		private float prevX;
		// текущее время (запоминается для таймера длинного нажатия)
		long ms;
		// позиция карты в списке (той, на которую пришлось касание)
		private int pos;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (chousenCards.size() == 0)
				return true;
			// фиксируем координаты касания
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: // нажатие
				// запоминаем, где было нажатие
				prevY = y;
				prevX = x;
				// запоминаем время для таймера длинного нажатия
				ms = new Date().getTime();
				// включаем флаг перетаскивания
				f = true;

				// получаем номера в списке для первой и последней видимой карты
				int first = list.getFirstVisiblePosition();
				int last = list.getLastVisiblePosition();

				// вычисляем позицию карты, на которую было нажатие
				for (int i = first; i <= last; i++) {
					View v1 = list.getChildAt(i);
					int[] location = new int[2];
					v1.getLocationOnScreen(location);
					if ((y > v1.getTop()) && (y < v1.getBottom())) {
						pos = i;
						break;
					}
				}

				// карта, на которую было нажатие -
				// потенциальная для показывания
				// запоминаем ее и заводим для нее таймер
				cardShow = chousenCards.get(pos);
				ShowCardTask task = new ShowCardTask();
				timer = new Timer();
				timer.schedule(task, 333);
				break;
			case MotionEvent.ACTION_MOVE: // перемещение
				// скролл отключен
				// if (Math.abs(y - prevY) > 50) {
				// list.scrollTo(0, (int) (prevY - y));
				// timer.cancel();
				// }

				// если тапают вправо, возвращам карту в исходное положение,
				// если
				// до этого она была сохранена
				if (((x - prevX) > v.getWidth() / 6) && f) {
					timer.cancel();
					f = false;
					if (savedCards.get(pos))
						unsaveCard(pos);
				}
				// если тапают влево, откладываем карту в сторону
				if (((prevX - x) > v.getWidth() / 6) && f) {
					timer.cancel();
					if (!savedCards.get(pos)) {
						saveCard(pos);
					}
					f = false;
				}
				// если тапают вверх, убираем карту в колоду
				if (((prevY - y) > v.getHeight() / 4) && f) {
					f = false;
					timer.cancel();
					ret(pos);
				}
				break;
			case MotionEvent.ACTION_UP: // отпускание
				// если не было длинного нажатия
				// (если было - сработает задание по таймеру)
				if ((new Date().getTime() - ms) < 333) {
					f = false;
					timer.cancel();
					// если не было тапа, переворачиваем карту
					if ((Math.abs(x - prevX) < v.getWidth() * 0.01)
							&& (Math.abs(y - prevY) < v.getHeight() * 0.01)) {
						flip(pos);
					}
				}
				break;
			}
			return true;
		}

	}

	// асинхронное задание для показа карт при длинном нажатии
	public class ShowCardTask extends TimerTask {
		public void run() {
			Looper.prepare();
			show(cardShow);
			Looper.loop();
		}
	}

	// асинхронное задание для выбора каты дня при длинном нажатии
	public class ChooseDayCardTask extends TimerTask {
		public void run() {
			Looper.prepare();
			chooseDayCard();
			Looper.loop();
		}
	}

	// сделать колоду карт
	private void setUpCards() {
		cards.add(new Card("1", "red", R.drawable.r1, R.drawable.r1s,
				"Личность, человек, интроспекция, существо"));
		cards.add(new Card("2", "red", R.drawable.r2, R.drawable.r2s,
				"Преграда, отграничивание, защита"));
		cards.add(new Card("3", "red", R.drawable.r3, R.drawable.r3s,
				"Действие, реализация, творчество, форма, образ"));
		cards.add(new Card("4", "red", R.drawable.r4, R.drawable.r4s,
				"Воля, эмоции, интенсивность"));
		cards.add(new Card("5", "red", R.drawable.r5, R.drawable.r5s,
				"Встреча, пара, отношение"));
		cards.add(new Card("6", "red", R.drawable.r6, R.drawable.r6s,
				"Владение, собственность"));
		cards.add(new Card("7", "red", R.drawable.r7, R.drawable.r7s,
				"Прошлое, связность, причина-следствие, +маятник, интуиция"));
		cards.add(new Card("8", "red", R.drawable.r8, R.drawable.r8s,
				"Дорога, путь, движение, развитие, изменение"));
		cards.add(new Card("9", "red", R.drawable.r9, R.drawable.r9s,
				"Стихия, мир, жизнь, сродство"));
		cards.add(new Card("10", "red", R.drawable.r10, R.drawable.r10s,
				"Закон, уклад, естественность, совершенство"));
		cards.add(new Card("13", "red", R.drawable.r13, R.drawable.r13s,
				"Рассвет, изменение, перемены, выход из контекста"));
		cards.add(new Card("17", "red", R.drawable.r17, R.drawable.r17s,
				"Ткань творения, сила, сейчас"));

		cards.add(new Card("1", "black", R.drawable.b1, R.drawable.b1s,
				"Сомнение, слабость, неуверенность, отчаяние"));
		cards.add(new Card("2", "black", R.drawable.b2, R.drawable.b2s,
				"Страх, неприятие, замкнутость?"));
		cards.add(new Card("3", "black", R.drawable.b3, R.drawable.b3s,
				"Маска, ложь, игра"));
		cards.add(new Card("4", "black", R.drawable.b4, R.drawable.b4s,
				"Поток, подверженность, подавление воли, контроль, вовлеченность"));
		cards.add(new Card("5", "black", R.drawable.b5, R.drawable.b5s,
				"Поединок, противоречие, две воли, противостояние, боль"));
		cards.add(new Card("6", "black", R.drawable.b6, R.drawable.b6s,
				"Разделение, безразличие, различение, общность"));
		cards.add(new Card("7", "black", R.drawable.b7, R.drawable.b7s,
				"Неожиданность, неучтённое, случай, +туман, сокрытое"));
		cards.add(new Card("8", "black", R.drawable.b8, R.drawable.b8s,
				"Перекрёсток, выбор, альтернатива, равнозначность"));
		cards.add(new Card("9", "black", R.drawable.b9, R.drawable.b9s,
				"Бездна мира, страсть, тьма"));
		cards.add(new Card("10", "black", R.drawable.b10, R.drawable.b10s,
				"Лезвие, напряжение, разрушение"));
		cards.add(new Card("13", "black", R.drawable.b13, R.drawable.b13s,
				"Статика, смерть"));
		cards.add(new Card("17", "black", R.drawable.b17, R.drawable.b17s,
				"Холод, небытие, время"));

		cards.add(new Card("0", "", R.drawable.zero, R.drawable.zeros,
				"Творение, свобода, новая судьба"));
	}
}
