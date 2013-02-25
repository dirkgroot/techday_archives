package nl.avisi.client.widget;

import nl.avisi.shared.domain.Pizza;
import org.jboss.errai.ui.client.widget.ListWidget;

import javax.enterprise.context.Dependent;

@Dependent
public class PizzaListWidget extends ListWidget<Pizza, PizzaWidget> {

//    public PizzaListWidget() {
//        super(new HTMLPanel("table", ""));
//    }

    @Override
    protected Class<PizzaWidget> getItemWidgetType() {
        return PizzaWidget.class;
    }
}
