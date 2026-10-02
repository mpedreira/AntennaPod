package de.danoeh.antennapod.ui.screen.subscriptions;

import android.content.Context;
import androidx.annotation.NonNull;
import de.danoeh.antennapod.R;
import de.danoeh.antennapod.model.feed.Feed;
import de.danoeh.antennapod.storage.database.DBReader;
import de.danoeh.antennapod.storage.database.DBWriter;
import de.danoeh.antennapod.ui.preferences.screen.ReorderDialog;
import de.danoeh.antennapod.ui.preferences.screen.ReorderDialogItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubscriptionOrderDialog extends ReorderDialog {
    public SubscriptionOrderDialog(Context context) {
        super(context);
    }

    @Override
    protected int getTitle() {
        return R.string.reorder_subscriptions;
    }

    @NonNull
    @Override
    protected List<ReorderDialogItem> getInitialItems() {
        List<Feed> feeds = new ArrayList<>(DBReader.getFeedList());
        Collections.sort(feeds, (f1, f2) -> {
            int p1 = f1.getPreferences() != null ? f1.getPreferences().getPriority() : 0;
            int p2 = f2.getPreferences() != null ? f2.getPreferences().getPriority() : 0;
            if (p1 != p2) {
                return Integer.compare(p1, p2);
            }
            return f1.getTitle().compareToIgnoreCase(f2.getTitle());
        });
        List<ReorderDialogItem> items = new ArrayList<>();
        for (Feed feed : feeds) {
            items.add(new ReorderDialogItem(ReorderDialogItem.ViewType.Section,
                    String.valueOf(feed.getId()), feed.getTitle()));
        }
        return items;
    }

    @Override
    protected void onReset() {
        List<Feed> feeds = new ArrayList<>(DBReader.getFeedList());
        Collections.sort(feeds, (f1, f2) -> Long.compare(f1.getId(), f2.getId()));
        List<Long> feedIds = new ArrayList<>();
        for (Feed feed : feeds) {
            feedIds.add(feed.getId());
        }
        DBWriter.setFeedOrder(feedIds);
    }

    @Override
    protected void onConfirmed() {
        List<Long> feedIds = new ArrayList<>();
        for (String tag : getTagsWithoutHeaders()) {
            feedIds.add(Long.parseLong(tag));
        }
        DBWriter.setFeedOrder(feedIds);
    }
}
