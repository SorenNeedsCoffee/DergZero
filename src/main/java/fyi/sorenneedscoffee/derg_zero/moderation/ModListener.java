package fyi.sorenneedscoffee.derg_zero.moderation;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ModListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        ModUtil.setJda(event.getJDA());
    }
}
