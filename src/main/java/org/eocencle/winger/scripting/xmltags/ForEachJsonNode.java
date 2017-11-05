package org.eocencle.winger.scripting.xmltags;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eocencle.winger.parsing.GenericTokenParser;
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
	private Configuration configuration;

	public ForEachJsonNode(Configuration configuration, JsonNode contents, String collectionExpression, String index,
			String item, String open, String close, String separator) {
		this.collectionExpression = collectionExpression;
		this.contents = contents;
		this.open = open;
		this.close = close;
		this.separator = separator;
		this.index = index;
		this.item = item;
		this.configuration = configuration;
	}

	public boolean apply(DynamicContext context) {
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
				ForEachJsonNode.PrefixedContext arg10;
				if (first) {
					arg10 = new ForEachJsonNode.PrefixedContext(context, "");
				} else if (this.separator != null) {
					arg10 = new ForEachJsonNode.PrefixedContext(context, this.separator);
				} else {
					arg10 = new ForEachJsonNode.PrefixedContext(context, "");
				}

				int uniqueNumber = arg10.getUniqueNumber();
				if (o instanceof Entry) {
					Entry mapEntry = (Entry) o;
					this.applyIndex(arg10, mapEntry.getKey(), uniqueNumber);
					this.applyItem(arg10, mapEntry.getValue(), uniqueNumber);
				} else {
					this.applyIndex(arg10, Integer.valueOf(i), uniqueNumber);
					this.applyItem(arg10, o, uniqueNumber);
				}

				this.contents.apply(new ForEachJsonNode.FilteredDynamicContext(this.configuration, arg10, this.index,
						this.item, uniqueNumber));
				if (first) {
					first = !((ForEachJsonNode.PrefixedContext) arg10).isPrefixApplied();
				}

				context = context;
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

	private void applyOpen(DynamicContext context) {
		if (this.open != null) {
			context.appendSql(this.open);
		}

	}

	private void applyClose(DynamicContext context) {
		if (this.close != null) {
			context.appendSql(this.close);
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
			super(ForEachJsonNode.this.configuration, (Object) null);
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

		public void appendSql(String sql) {
			if (!this.prefixApplied && sql != null && sql.trim().length() > 0) {
				this.delegate.appendSql(this.prefix);
				this.prefixApplied = true;
			}

			this.delegate.appendSql(sql);
		}

		public String getSql() {
			return this.delegate.getSql();
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

		public String getSql() {
			return this.delegate.getSql();
		}

		public void appendSql(String sql) {
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
			this.delegate.appendSql(parser.parse(sql));
		}

		public int getUniqueNumber() {
			return this.delegate.getUniqueNumber();
		}
	}
}
