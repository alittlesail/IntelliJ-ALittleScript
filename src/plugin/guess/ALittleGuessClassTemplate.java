package plugin.guess;

import plugin.psi.ALittleTemplatePairDec;

public class ALittleGuessClassTemplate extends ALittleGuessTemplate {
    public ALittleGuessClassTemplate(ALittleTemplatePairDec p_template_pair_dec
            , ALittleGuess p_template_extends
            , boolean p_is_class, boolean p_is_struct)
    {
        super(p_template_pair_dec, p_template_extends, p_is_class, p_is_struct);
    }

    @Override
    public ALittleGuess clone()
    {
        ALittleGuessClassTemplate guess = new ALittleGuessClassTemplate(template_pair_dec, template_extends, is_class, is_struct);
        guess.updateValue();
        return guess;
    }
}
