package ori.cards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class DialogScreen {

	public static final int ABOUT = 1;
	public static final int CARDINFO = 2;
	public static final int DAYCARD = 3;
	static AlertDialog.Builder builder;

	public static AlertDialog getDialog(final MainActivity activity, int ID,
			Card card) {
		builder = new AlertDialog.Builder(activity);
		switch (ID) {
		case DAYCARD:
			View view = activity.getLayoutInflater().inflate(R.layout.daycard,
					null);
			builder.setView(view);
			ImageView v = (ImageView) view.findViewById(R.id.mainImg);
			if (activity.hasDayCard)
				v.setImageResource(activity.dayCard.getPicture());
			v.setOnTouchListener(new OnTouchListener() {
				private boolean f = false;
				private float prevY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					float y = event.getY();
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // �������
						prevY = y;
						f = true;
						break;
					case MotionEvent.ACTION_MOVE: // ��������
						if (((y - prevY) > v.getHeight() / 2) && f) {
							activity.retDayCard();
							activity.takeDayCard();
							activity.chooseDayCard();
							f = false;
						}
						break;
					}
					return true;
				}

			});
			if (activity.hasDayCard)
				builder.setTitle("Карта дня - " + card.getName() + " "
						+ card.getColor());
			else
				builder.setTitle("Карта дня");
			builder.setCancelable(true);
			if (activity.hasDayCard)
				builder.setNeutralButton("Вернуть карту дня в колоду",
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								activity.retDayCard();
								activity.chooseDayCard();
							}

						});
			return builder.create();
		default:
			return null;
		}
	}

	public static AlertDialog _getDialog(final Activity activity, int ID,
			Card card) {
		builder = new AlertDialog.Builder(activity);
		switch (ID) {
		case ABOUT:
			builder.setTitle("Инструкции");
			builder.setMessage("Вытащить карту - тап по колоде вниз.\n"
					+ "Сохранить карту - тап по карте влево.\n"
					+ "Вернуть карту в общий список - тап по карте вправо.\n"
					+ "Тап по карте вверх - убрать карту обратно в колоду.\n"
					+ "Перевернуть карту - клик по карте.\n"
					+ "Клик по колоде - смена режима вытаскивания карт.\n"
					+ "Долгий клик на колоде - посмотреть/вытащить карту дня.\n"
					+ "Тап по колоде вверх - убрать все несохраненные карты обратно в колоду.\n"
					+ "Долгий клик на карте - посмотреть ее полное изображение и текстовое пояснение.\n"
					+ "This aptitude does not have Super Cow Powers.\n");
			builder.setCancelable(true);
			return builder.create();
		case CARDINFO:
			ImageView image = new ImageView(activity);
			image.setImageResource(card.getPicture());
			builder.setTitle(card.getName() + " " + card.getColor());
			builder.setMessage(card.getDescr());
			builder.setCancelable(true);
			builder.setView(image);
			return builder.create();
		default:
			return null;
		}
	}
	
}