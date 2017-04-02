package de.neemann.digital.gui.components.table;

import de.neemann.digital.analyse.expression.Expression;
import de.neemann.digital.analyse.expression.ExpressionException;
import de.neemann.digital.analyse.expression.format.FormatterException;

import java.util.ArrayList;

/**
 * ExpressionListenerStore stores the generated expressions for later use.
 * <p>
 * Created by hneemann on 11.03.17.
 */
public class ExpressionListenerStore implements ExpressionListener {

    private ExpressionListener parent;
    private ArrayList<Result> results;
    private boolean closed;

    /**
     * Creates a new instance
     *
     * @param parent the {@link ExpressionListener} which is to fill
     */
    public ExpressionListenerStore(ExpressionListener parent) {
        this.parent = parent;
        this.results = new ArrayList<>();
        closed = false;
    }

    @Override
    public void resultFound(String name, Expression expression) throws FormatterException, ExpressionException {
        results.add(new Result(name, expression));
        if (parent != null)
            parent.resultFound(name, expression);
    }

    @Override
    public void close() throws FormatterException, ExpressionException {
        closed = true;
        if (parent != null)
            parent.close();
    }

    /**
     * Puts the results to the given listener
     *
     * @param listener the listener to fill
     * @throws FormatterException  FormatterException
     * @throws ExpressionException ExpressionException
     */
    public void replayTo(ExpressionListener listener) throws FormatterException, ExpressionException {
        if (!closed)
            throw new ExpressionException("ExpressionListenerStore not closed");

        for (Result r : results)
            listener.resultFound(r.name, r.expression.copy());
        listener.close();
    }

    /**
     * @return the first found expression
     */
    public Expression getFirst() {
        return results.get(0).expression.copy();
    }

    private static final class Result {
        private final String name;
        private final Expression expression;

        private Result(String name, Expression expression) {
            this.name = name;
            this.expression = expression;
        }
    }
}
