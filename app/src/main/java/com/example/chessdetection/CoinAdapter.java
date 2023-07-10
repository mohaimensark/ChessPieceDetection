package com.example.chessdetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.List;
public class CoinAdapter extends ArrayAdapter<Coin> {

    private LayoutInflater inflater;
    private DecimalFormat priceFormatter;

    public CoinAdapter(Context context, List<Coin> coinList) {
        super(context, 0, coinList);
        inflater = LayoutInflater.from(context);
        priceFormatter = new DecimalFormat("#,##0.00");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_view, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Coin coin = getItem(position);
        if (coin != null) {
            holder.symbolTextView.setText("Symbol: "+coin.getSymbol());
            holder.nameTextView.setText("Name: "+coin.getName());
            holder.rankTextView.setText("Rank: "+String.valueOf(coin.getRank()));
            holder.priceTextView.setText("Price in Doller: $" + priceFormatter.format(coin.getPriceUSD()));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView symbolTextView;
        TextView nameTextView;
        TextView rankTextView;
        TextView priceTextView;

        ViewHolder(View itemView) {
            symbolTextView = itemView.findViewById(R.id.symbolTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
        }
    }
}
