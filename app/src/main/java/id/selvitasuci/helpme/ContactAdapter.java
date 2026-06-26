package id.selvitasuci.helpme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import id.selvitasuci.helpme.data.Contact;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    public interface OnCallListener   { void onCall(Contact contact, int position); }
    public interface OnDeleteListener { void onDelete(Contact contact, int position); }

    private final List<Contact>    contacts;
    private final OnCallListener   callListener;
    private final OnDeleteListener deleteListener;

    // Avatar background colors cycling
    private static final int[] AVATAR_COLORS = {
            0xFF1565C0, 0xFF2E7D32, 0xFF6A1B9A,
            0xFF00838F, 0xFFE65100, 0xFF880E4F
    };

    public ContactAdapter(List<Contact> contacts,
                          OnCallListener callListener,
                          OnDeleteListener deleteListener) {
        this.contacts       = contacts;
        this.callListener   = callListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);

        // Avatar letter
        String name = contact.getName();
        String avatarLetter = name.isEmpty() ? "?" : String.valueOf(name.charAt(0)).toUpperCase();
        holder.tvAvatar.setText(avatarLetter);

        // Avatar circle color
        int color = AVATAR_COLORS[position % AVATAR_COLORS.length];
        holder.avatarCircle.setBackgroundColor(color);
        holder.avatarCircle.setBackgroundTintList(null);

        // Draw a colored circle programmatically
        android.graphics.drawable.GradientDrawable circle = new android.graphics.drawable.GradientDrawable();
        circle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        circle.setColor(color);
        holder.avatarCircle.setBackground(circle);

        holder.tvContactName.setText(contact.getName());
        holder.tvContactPhone.setText(contact.getPhone());
        holder.tvContactRelation.setText(
                contact.getRelation().isEmpty() ? "Kontak" : contact.getRelation());

        holder.btnCall.setOnClickListener(v -> {
            if (callListener != null) callListener.onCall(contact, position);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(contact, position);
        });
    }

    @Override
    public int getItemCount() { return contacts.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView    tvAvatar, tvContactName, tvContactPhone, tvContactRelation;
        View        avatarCircle;
        ImageButton btnCall, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar          = itemView.findViewById(R.id.tvAvatar);
            tvContactName     = itemView.findViewById(R.id.tvContactName);
            tvContactPhone    = itemView.findViewById(R.id.tvContactPhone);
            tvContactRelation = itemView.findViewById(R.id.tvContactRelation);
            avatarCircle      = itemView.findViewById(R.id.viewAvatarBg);
            btnCall           = itemView.findViewById(R.id.btnCall);
            btnDelete         = itemView.findViewById(R.id.btnDelete);
        }
    }
}
