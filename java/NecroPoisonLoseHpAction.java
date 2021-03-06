package com.megacrit.cardcrawl.actions.unique;

import com.megacrit.cardcrawl.actions.*;
import com.megacrit.cardcrawl.core.*;
import com.megacrit.cardcrawl.dungeons.*;
import com.megacrit.cardcrawl.rooms.*;
import com.megacrit.cardcrawl.vfx.combat.*;
import com.megacrit.cardcrawl.monsters.*;
import com.badlogic.gdx.graphics.*;
import com.megacrit.cardcrawl.cards.*;
import com.megacrit.cardcrawl.characters.*;
import com.megacrit.cardcrawl.unlock.*;
import com.megacrit.cardcrawl.actions.utility.*;
import com.megacrit.cardcrawl.powers.*;
import org.apache.logging.log4j.*;

public class NecroPoisonLoseHpAction extends AbstractGameAction
{
    private static final Logger logger;
    private static final float DURATION = 0.33f;
    
    public NecroPoisonLoseHpAction(final AbstractCreature target, final AbstractCreature source, final int amount, final AttackEffect effect) {
        this.setValues(target, source, amount);
        this.actionType = ActionType.DAMAGE;
        this.attackEffect = effect;
        this.duration = 0.33f;
    }
    
    @Override
    public void update() {
        if (AbstractDungeon.getCurrRoom().phase != AbstractRoom.RoomPhase.COMBAT) {
            this.isDone = true;
            return;
        }
        if (this.duration == 0.33f && this.target.currentHealth > 0) {
            NecroPoisonLoseHpAction.logger.info(this.target.name + " HAS " + this.target.currentHealth + " HP.");
            this.target.damageFlash = true;
            this.target.damageFlashFrames = 4;
            AbstractDungeon.effectList.add(new FlashAtkImgEffect(this.target.hb.cX, this.target.hb.cY, this.attackEffect));
        }
        this.tickDuration();
        if (this.isDone) {
            if (this.target.currentHealth > 0) {
                this.target.tint.color = Color.CHARTREUSE.cpy();
                this.target.tint.changeColor(Color.WHITE.cpy());
                this.target.damage(new DamageInfo(this.source, this.amount, DamageInfo.DamageType.HP_LOSS));
            }
            if (!AbstractDungeon.player.hasPower("Venomology") || this.target.isPlayer) {
                final AbstractPower p = this.target.getPower("Necrotic Poison");
                if (p != null) {
                    final AbstractPower abstractPower = p;
					if (abstractPower.amount > 3) {
						abstractPower.amount -= (abstractPower.amount / 2);
					} else {
						--abstractPower.amount;
					}
                    if (p.amount == 0) {
                        this.target.powers.remove(p);
                    }
                    else {
                        p.updateDescription();
                    }
                }
            }
            else if (AbstractDungeon.player.hasPower("Venomology") && !this.target.isPlayer) {
                final AbstractPower p = this.target.getPower("Necrotic Poison");
                if (p != null) {
                    final AbstractPower abstractPower2 = p;
                    abstractPower2.amount += AbstractDungeon.player.getPower("Venomology").amount;
                }
            }
            if (AbstractDungeon.getCurrRoom().monsters.areMonstersBasicallyDead()) {
                AbstractDungeon.actionManager.clearPostCombatActions();
            }
            AbstractDungeon.actionManager.addToTop(new WaitAction(0.1f));
        }
    }
    
    static {
        logger = LogManager.getLogger(NecroPoisonLoseHpAction.class.getName());
    }
}