package org.eocencle.winger.xmltags;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eocencle.winger.exceptions.WingerException;
import org.eocencle.winger.parsing.GenericTokenParser;
import org.eocencle.winger.parsing.TokenHandler;
import org.eocencle.winger.session.Configuration;

public class ForEachJsonNode implements JsonNode {
	public static final String ITEM_PREFIX = "__frch_";
	private ExpressionEvaluator evaluator = new ExpressionEvaluator();
	private String collectionExpression;
	private JsonNode contents;
	private String open;
	private String close;
	private String separator;
	private String item;
	private String index;
	private Configuration config;

	public ForEachJsonNode(Configuration config, JsonNode contents, String collectionExpression, String index,
			String item, String open, String close, String separator) {
		this.collectionExpression = collectionExpression;
		this.contents = contents;
		this.open = open;
		this.close = close;
		this.separator = separator;
		this.index = index;
		this.item = item;
		this.config = config;
	}

	public boolean apply(DynamicContext context) throws WingerException {
		Map bindings = context.getBindings();
		Iterable iterable = this.evaluator.evaluateIterable(this.collectionExpression, bindings);
		if (!iterable.iterator().hasNext()) {
			return true;
		} else {
			boolean first = true;
			this.applyOpen(context);
			int i = 0;

			for (Iterator i$ = iterable.iterator(); i$.hasNext(); ++i) {
				Object o = i$.next();
				ForEachJsonNode.PrefixedContext prefixedContext;
				if (first) {
					prefixedContext = new ForEachJsonNode.PrefixedContext(context, "");
				} else if (this.separator != null) {
					prefixedContext = new ForEachJsonNode.PrefixedContext(context, this.separator);
				} else {
					prefixedContext = new ForEachJsonNode.PrefixedContext(context, "");
				}

				int uniqueNumber = prefixedContext.getUniqueNumber();
				if (o instanceof Entry) {
					Entry mapEntry = (Entry) o;
					this.applyIndex(prefixedContext, mapEntry.getKey(), uniqueNumber);
					this.applyItem(prefixedContext, mapEntry.getValue(), uniqueNumber);
				} else {
					this.applyIndex(prefixedContext, Integer.valueOf(i), uniqueNumber);
					this.applyItem(prefixedContext, o, uniqueNumber);
				}

				this.contents.apply(new ForEachJsonNode.FilteredDynamicContext(this.config, prefixedContext, this.index,
						this.item, uniqueNumber));
				if (first) {
					first = !((ForEachJsonNode.PrefixedContext) prefixedContext).isPrefixApplied();
				}
			}

			this.applyClose(context);
			return true;
		}
	}

	private void applyIndex(DynamicContext context, Object o, int i) {
		if (this.index != null) {
			context.bind(this.index, o);
			context.bind(itemizeItem(this.index, i), o);
		}

	}

	private void applyItem(DynamicContext context, Object o, int i) {
		if (this.item != null) {
			context.bind(this.item, o);
			context.bind(itemizeItem(this.item, i), o);
		}

	}

	private void applyOpen(DynamicContext context) throws WingerException {
		if (this.open != null) {
			context.appendJson(this.open);
		}

	}

	private void applyClose(DynamicContext context) throws WingerException {
		if (this.close != null) {
			context.appendJson(this.close);
		}

	}

	private static String itemizeItem(String item, int i) {
		return "__frch_" + item + "_" + i;
	}

	private class PrefixedContext extends DynamicContext {
		private DynamicContext delegate;
		private String prefix;
		private boolean prefixApplied;

		public PrefixedContext(DynamicContext delegate, String prefix) {
			super(ForEachJsonNode.this.config, (Object) null);
			this.delegate = delegate;
			this.prefix = prefix;
			this.prefixApplied = false;
		}

		public boolean isPrefixApplied() {
			return this.prefixApplied;
		}

		public Map<String, Object> getBindings() {
			return this.delegate.getBindings();
		}

		public void bind(String name, Object value) {
			this.delegate.bind(name, value);
		}

		public void appendJson(String json) throws WingerException {
			if (!this.prefixApplied && json != null && json.trim().length() > 0) {
				this.delegate.appendJson(this.prefix);
				this.prefixApplied = true;
			}

			this.delegate.appendJson(json);
		}

		public String getJson() {
			return this.delegate.getJson();
		}

		public int getUniqueNumber() {
			return this.delegate.getUniqueNumber();
		}
	}

	private static class FilteredDynamicContext extends DynamicContext {
		private DynamicContext delegate;
		private int index;
		private String itemIndex;
		private String item;

		public FilteredDynamicContext(Configuration configuration, DynamicContext delegate, String itemIndex,
				String item, int i) {
			super(configuration, (Object) null);
			this.delegate = delegate;
			this.index = i;
			this.itemIndex = itemIndex;
			this.item = item;
		}

		public Map<String, Object> getBindings() {
			return this.delegate.getBindings();
		}

		public void bind(String name, Object value) {
			this.delegate.bind(name, value);
		}

		public String getJson() {
			return this.delegate.getJson();
		}

		public void appendJson(String json) throws WingerException {
			GenericTokenParser parser = new GenericTokenParser("#{", "}", new TokenHandler() {
				public String handleToken(String content) {
					String newContent = content
							.replaceFirst("^\\s*" + FilteredDynamicContext.this.item + "(?![^.,:\\s])", ForEachJsonNode
									.itemizeItem(FilteredDynamicContext.this.item, FilteredDynamicContext.this.index));
					if (FilteredDynamicContext.this.itemIndex != null && newContent.equals(content)) {
						newContent = content.replaceFirst(
								"^\\s*" + FilteredDynamicContext.this.itemIndex + "(?![^.,:\\s])",
								ForEachJsonNode.itemizeItem(FilteredDynamicContext.this.itemIndex,
										FilteredDynamicContext.this.index));
					}

					return "#{" + newContent + "}";
				}
			});
			this.delegate.appendJson(parser.parse(json));
		}

		public int getUniqueNumber() {
			return this.delegate.getUniqueNumber();
		}
	}
}
