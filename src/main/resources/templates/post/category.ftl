<#include "/inc/layout.ftl" />

<@layout "帖子分类">

  <#include "/inc/header-panel.ftl" />

<div class="layui-container">
  <div class="layui-row layui-col-space15">
    <div class="layui-col-md8">
      <div class="fly-panel" style="margin-bottom: 0;">
        
        <div class="fly-panel-title fly-filter">
            <a href="?orderMode=0" class="${(orderMode == 0)?string('layui-hide-xs layui-this', '')}">按最新</a>
            <span class="fly-mid"></span>
            <a href="?orderMode=1" class="${(orderMode == 1)?string('layui-hide-xs layui-this', '')}">按热议</a>
          </span>
        </div>
		
		<!--这段代码有发起请求  在template包中-->
        <@posts categoryId=currentCategoryId pn=pn size=5 orderMode=orderMode>

          <ul class="fly-list">
              <#list results.records as post>
                <@plisting post></@plisting>
              </#list>
          </ul>

          <@paging results></@paging>
		</@posts>
        


      </div>
    </div>

    <#include "/inc/right.ftl" />

  </div>
</div>
<script>
  layui.cache.page = 'jie';
</script>

</@layout>