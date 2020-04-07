<div class="layui-col-md4">

    <dl class="fly-panel fly-list-one">
        <dt class="fly-panel-title">本周热议</dt>

        <@hots>
            <#list results as post>
                <dd>
                    <a href="/post/${post.id}">${post.title}</a>
                    <span><i class="iconfont icon-pinglun1"></i> ${post.commentCount}</span>
                </dd>
            </#list>
        </@hots>

        <!-- 无数据时 -->
        <!--
        <div class="fly-none">没有相关数据</div>
        -->
    </dl>

    <div class="fly-panel">
        <div class="fly-panel-title">
            这里可作为广告区域
        </div>
        <div class="fly-panel-main">
        	<img src="/res/images/guangcai.jpg" width="350px" time-limit="2017.09.25-2099.01.01" style="background-color: #5FB878;" alt="guangcai">
        </div>
    </div>

    <div class="fly-panel fly-link">
        <h3 class="fly-panel-title">友情链接</h3>
        <dl class="fly-panel-main">
            <dd><a href="http://www.gdufe.edu.cn/main.htm" target="_blank">广财官网</a><dd>
            <dd><a href="http://jwxt.gdufe.edu.cn/jsxsd/" target="_blank">教务系统</a><dd>
            <dd><a href="http://my.gdufe.edu.cn/" target="_blank">信息门户</a><dd>
            <dd><a href="http://sztz.gdufe.edu.cn/sztz/" target="_blank">素拓系统</a><dd>
            <dd><a href="#" class="fly-link">申请友链</a><dd>
        </dl>
    </div>

</div>