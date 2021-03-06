package com.megacrit.cardcrawl.events.thebottom;

import com.megacrit.cardcrawl.audio.SoundMaster;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.red.Bash;
import com.megacrit.cardcrawl.cards.green.Neutralize;
import com.megacrit.cardcrawl.cards.green.Survivor;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.vfx.UpgradeShineEffect;
import com.megacrit.cardcrawl.vfx.cardManip.PurgeCardEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.relics.*;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.unlock.UnlockTracker;
import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;

public class MirrorMist
  extends AbstractImageEvent
{
  public static final String ID = "Mirror Mist";
  private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString("Mirror Mist");
  public static final String NAME = eventStrings.NAME;
  public static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
  public static final String[] OPTIONS = eventStrings.OPTIONS;
  private static final String DIALOG_1 = DESCRIPTIONS[0];
  private static final String RESULT_DIALOG_A = DESCRIPTIONS[1];
  private static final String RESULT_DIALOG_B = DESCRIPTIONS[2];
  private CurScreen screen = CurScreen.INTRO;
  private boolean hasBash = false;
  private boolean hasRing = false;
  private boolean sizzling = false;
  
  private static enum CurScreen
  {
    INTRO,  RESULT;
    
    private CurScreen() {}
  }
  
  
  public MirrorMist()
  {
    super(NAME, DIALOG_1, "images/events/livingWall.jpg");
    
	this.hasBash = CardHelper.hasCardWithID("Bash");
	this.hasRing = AbstractDungeon.player.hasRelic("Ring of the Snake");
	
	//if (AbstractDungeon.ascensionLevel > 15) {
		this.sizzling = true;
	//}
	
	String bashName = new Bash().name;
	String ringName = new SnakeRing().name;
	if (this.hasBash)
	{
		GenericEventDialog.setDialogOption(OPTIONS[2] + bashName + OPTIONS[3]);
	} else {
		GenericEventDialog.setDialogOption(OPTIONS[0] + bashName + OPTIONS[1], true);
	}
	if (this.hasRing)
	{
		if (this.sizzling) {
			GenericEventDialog.setDialogOption(OPTIONS[2] + ringName + OPTIONS[7]);
		} else {
			GenericEventDialog.setDialogOption(OPTIONS[2] + ringName + OPTIONS[4]);
		}
	} else {
		GenericEventDialog.setDialogOption(OPTIONS[0] + ringName + OPTIONS[1], true);
	}
    GenericEventDialog.setDialogOption(OPTIONS[5]);
  }

  protected void buttonEffect(int buttonPressed)
  {
    switch (this.screen)
    {
    case INTRO: 
      switch (buttonPressed)
      {
      case 0: 
        //logMetric("Bash to Survivor+Neutralize");
		GenericEventDialog.updateBodyText(RESULT_DIALOG_A);
        CardCrawlGame.sound.play("CARD_EXHAUST");
        AbstractDungeon.effectList.add(new PurgeCardEffect(new Bash()));
        AbstractDungeon.player.masterDeck.removeCard("Bash");
		AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Neutralize(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
		UnlockTracker.markCardAsSeen("Neutralize");
		AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(new Survivor(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
		UnlockTracker.markCardAsSeen("Survivor");
        break;
      case 1: 
        //logMetric("Ring to Blood");
		GenericEventDialog.updateBodyText(RESULT_DIALOG_A);
		AbstractDungeon.player.loseRelic("Ring of the Snake");
		if (!this.sizzling) {
			AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2, new BurningBlood());
		} else {
			AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2, Settings.HEIGHT / 2, new SizzlingBlood());
		}
        break;
      default: 
        //logMetric("Wander");
        GenericEventDialog.updateBodyText(RESULT_DIALOG_B);
		float displayCount = 0.0F;
		
		ArrayList<AbstractCard> cards = new ArrayList();
		for (AbstractCard c : CardGroup.getGroupWithoutBottledCards(AbstractDungeon.player.masterDeck).group) {
			cards.add(c);
		}
		
		AbstractCard card = (AbstractCard)cards.remove(MathUtils.random(cards.size() - 1));
		card.untip();
		card.unhover();
		AbstractDungeon.player.masterDeck.removeCard(card);
		AbstractDungeon.transformCard(card, true, AbstractDungeon.miscRng);
		if ((AbstractDungeon.screen != AbstractDungeon.CurrentScreen.TRANSFORM) && (AbstractDungeon.transformedCard != null))
		{
		  AbstractDungeon.effectsQueue.add(new ShowCardAndObtainEffect(
			AbstractDungeon.getTransformedCard(), Settings.WIDTH / 3.0F + displayCount, Settings.HEIGHT / 2.0F, false));
		  
		  displayCount += Settings.WIDTH / 6.0F;
		}
		AbstractDungeon.getCurrRoom().rewardPopOutTimer = 0.25F;
		
      }
      GenericEventDialog.clearAllDialogs();
      GenericEventDialog.setDialogOption(OPTIONS[6]);
      this.screen = CurScreen.RESULT;
      break;
    default: 
      openMap();
    }
  }
  
  public void logMetric(String actionTaken)
  {
    AbstractEvent.logMetric("Mirror Mist", actionTaken);
  }
}
