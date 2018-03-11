package com.megacrit.cardcrawl.potions;

import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.localization.LocalizedStrings;
import com.megacrit.cardcrawl.localization.PotionStrings;
import com.megacrit.cardcrawl.powers.EnvenomPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;
import java.util.ArrayList;

public class VenomPotion
  extends AbstractPotion
{
  public static final String POTION_ID = "Venom Potion";
  private static final PotionStrings potionStrings = CardCrawlGame.languagePack.getPotionString("Venom Potion");
  public static final String NAME = potionStrings.NAME;
  public static final String[] DESCRIPTIONS = potionStrings.DESCRIPTIONS;
  
  public VenomPotion()
  {
    super(NAME, "Venom Potion", AbstractPotion.PotionSize.S, AbstractPotion.PotionColor.POISON);
    if (AbstractDungeon.ascensionLevel >= 11) {
      this.potency = 1;
      this.description = (DESCRIPTIONS[0] + 1 + DESCRIPTIONS[1] + DESCRIPTIONS[2] + this.potency + DESCRIPTIONS[3]);
    } else {
      this.potency = 0;
	  this.description = (DESCRIPTIONS[0] + 1 + DESCRIPTIONS[1]);
    }
    this.isThrown = false;
    this.tips.add(new PowerTip(this.name, this.description));
	this.rarity = AbstractPotion.PotionRarity.RARE;
  }
  
  public void use(AbstractCreature target)
  {
    target = AbstractDungeon.player;
    if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
      AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, AbstractDungeon.player, new EnvenomPower(target, 1), 1));
	  if (this.potency > 0){
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(target, AbstractDungeon.player, new StrengthPower(target, -this.potency), -this.potency));
	  }
    }
  }
  
  public AbstractPotion makeCopy()
  {
    return new VenomPotion();
  }
  
  public int getPrice()
  {
    return 60;
  }
}