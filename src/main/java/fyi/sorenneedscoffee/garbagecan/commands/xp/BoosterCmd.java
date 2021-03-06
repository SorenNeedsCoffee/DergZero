package fyi.sorenneedscoffee.garbagecan.commands.xp;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import fyi.sorenneedscoffee.garbagecan.Main;
import fyi.sorenneedscoffee.garbagecan.boosters.BoosterManager;
import fyi.sorenneedscoffee.garbagecan.boosters.BoosterResult;
import fyi.sorenneedscoffee.garbagecan.boosters.data.DataContext;
import fyi.sorenneedscoffee.garbagecan.boosters.data.models.UserBooster;
import fyi.sorenneedscoffee.garbagecan.commands.AdminCommand;
import fyi.sorenneedscoffee.garbagecan.commands.XpCommand;
import fyi.sorenneedscoffee.garbagecan.moderation.util.ModUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.MarkdownUtil;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

/**
 * @author SorenNeedsCoffee (github.com/sorenneedscoffee)
 */
public class BoosterCmd extends XpCommand {
    protected static final DataContext context = BoosterManager.context;

    public BoosterCmd() {
        super();
        this.name = "booster";
        this.aliases = new String[]{"boosters"};
        this.help = "commands related to the booster system";
        this.children = new Command[]{
                new AddBoosterCmd(),
                new ViewBoostersCmd(),
                new GiveBoosterCmd(),
                new UseBoosterCmd()
        };
    }

    @Override
    protected void execute(CommandEvent event) {
        List<UserBooster> boosters = context.getUserBoosters(event.getAuthor().getId());
        boosters.sort(Comparator.comparingDouble(a -> a.multiplier));
        StringBuilder stringBuilder = new StringBuilder();

        if (boosters.isEmpty()) {
            event.replyError("You have no boosters.");
            return;
        }

        for (int i = 0; i < boosters.size(); i++) {
            stringBuilder.append(i + 1)
                    .append(" - ")
                    .append(boosters.get(i).toString())
                    .append("\n\n");
        }

        stringBuilder.append("If you want to use any of these boosters, type !>boosters use <id of any above booster>");

        event.reply(MarkdownUtil.codeblock(stringBuilder.toString()));
    }

    public static class AddBoosterCmd extends AdminCommand {

        public AddBoosterCmd() {
            this.name = "add";
            this.arguments = "<multiplier> <duration> <valid java chronounit>";
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] args = event.getArgs().split(" ");

            float multiplier = Float.parseFloat(args[0]);
            long duration = Long.parseLong(args[1]);
            ChronoUnit unit = ChronoUnit.valueOf(args[2]);

            BoosterResult result = Main.manager.add(multiplier, duration, unit, true);

            if (result == BoosterResult.ADDED) {
                event.replySuccess("The given booster is now active");
            } else if (result == BoosterResult.QUEUED) {
                event.replySuccess("The given booster has been added to the queue");
            }
        }
    }

    public static class ViewBoostersCmd extends AdminCommand {

        public ViewBoostersCmd() {
            this.name = "view";
            this.arguments = "<target (ping, id, name#discriminator>";
        }

        @Override
        protected void execute(CommandEvent event) {
            User target = ModUtil.getTarget(event.getArgs());

            List<UserBooster> boosters = context.getUserBoosters(target.getId());
            boosters.sort(Comparator.comparingDouble(a -> a.multiplier));
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < boosters.size(); i++) {
                stringBuilder.append(i + 1)
                        .append(" - ")
                        .append(boosters.get(i).toString())
                        .append("\n\n");
            }

            event.reply(MarkdownUtil.codeblock(stringBuilder.toString()));
        }
    }

    public static class GiveBoosterCmd extends AdminCommand {

        public GiveBoosterCmd() {
            this.name = "give";
            this.arguments = "<target (ping, id, name#discriminator | all> <multiplier> <duration> <valid java chronounit>";
        }

        @Override
        protected void execute(CommandEvent event) {
            String[] args = event.getArgs().split(" ");

            User target = ModUtil.getTarget(args[0]);
            float multiplier = Float.parseFloat(args[1]);
            long duration = Long.parseLong(args[2]);
            ChronoUnit unit = ChronoUnit.valueOf(args[3]);

            if (args[0].equals("all")) {
                for (Member member : event.getGuild().getMembers()) {
                    if (!(member.isFake() || member.getUser().isBot()))
                        context.saveUserBooster(new UserBooster(context.getNewUId(), member.getId(), multiplier, duration, unit));
                }
            } else {
                context.saveUserBooster(new UserBooster(context.getNewUId(), target.getId(), multiplier, duration, unit));
            }
        }
    }

    public static class UseBoosterCmd extends XpCommand {

        public UseBoosterCmd() {
            this.name = "use";
            this.arguments = "<booster id (use !>boosters to see your boosters)>";
        }

        @Override
        protected void execute(CommandEvent event) {
            if (event.getArgs().isBlank() || event.getArgs().isEmpty()) {
                event.replyError("Args cannot be blank!");
                return;
            }

            int index = Integer.parseInt(event.getArgs()) - 1;

            List<UserBooster> boosters = context.getUserBoosters(event.getAuthor().getId());
            boosters.sort(Comparator.comparingDouble(a -> a.multiplier));

            UserBooster selection = boosters.get(index);

            BoosterResult result = Main.manager.add(selection.multiplier, selection.duration, selection.unit, true);

            if (result == BoosterResult.ADDED) {
                event.replySuccess("Your " + selection.multiplier + "x booster is now active!");
                context.removeUserBooster(selection.id);
            } else if (result == BoosterResult.QUEUED) {
                event.replySuccess("Your " + selection.multiplier + "x booster has been queued.");
                context.removeUserBooster(selection.id);
            } else {
                event.replyError("There are no slots available and the queue is full.");
            }
        }
    }
}
