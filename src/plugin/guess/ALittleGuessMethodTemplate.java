package plugin.guess;

import plugin.psi.ALittleTemplatePairDec;

public class ALittleGuessMethodTemplate extends ALittleGuessTemplate {
    public ALittleGuessMethodTemplate(ALittleTemplatePairDec p_template_pair_dec
            , ALittleGuess p_template_extends
            , boolean p_is_class, boolean p_is_struct, boolean p_is_const) {
        super(p_template_pair_dec, p_template_extends, p_is_class, p_is_struct, p_is_const);
    }

    @Override
    public ALittleGuess clone() {
        ALittleGuessMethodTemplate guess = new ALittleGuessMethodTemplate(template_pair_dec, template_extends, is_class, is_struct, is_const);
        guess.updateValue();
        return guess;
    }
}
