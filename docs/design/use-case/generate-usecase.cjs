const fs = require('fs')
const path = require('path')
const sharp = require('C:/Users/李晓/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/sharp')

const outputDir = __dirname
const svgPath = path.join(outputDir, '系统用例图.svg')
const pngPath = path.join(outputDir, '系统用例图.png')

const groups = [
  { actor: '游客', heading: '公开浏览与账号入口', x: 370, bus: 155, items: ['浏览首页', '查询文章', '查看文章详情', '查看关于我', '查看留言板', '注册', '登录', '重置密码'] },
  { actor: '普通用户', heading: '登录后的互动与个人功能', x: 900, bus: 685, items: ['点赞或取消点赞', '提交评论', '提交留言', '维护个人资料', '上传头像', '修改密码', '查看喜欢的文章', '退出登录'] },
  { actor: '管理员', heading: '后台管理', x: 1430, bus: 1215, items: ['查看后台统计', '管理文章', '管理用户', '审核评论', '审核留言', '管理分类', '管理标签', '管理站点配置'] },
]

const esc = (value) => value.replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&apos;' }[c]))

function actor(x, label) {
  return `<g class="actor">
    <circle cx="${x}" cy="105" r="17"/>
    <path d="M${x} 122V170 M${x-33} 140L${x} 132L${x+33} 140 M${x} 170L${x-27} 205 M${x} 170L${x+27} 205"/>
    <text x="${x}" y="239" class="actor-label">${esc(label)}</text>
  </g>`
}

function group(g) {
  const left = g.x - 190
  const cases = g.items.map((item, index) => {
    const y = 412 + index * 84
    return `<path class="association" d="M${g.bus} ${y}H${left}"/>
      <ellipse class="usecase" cx="${g.x}" cy="${y}" rx="190" ry="31"/>
      <text class="usecase-label" x="${g.x}" y="${y + 1}">${esc(item)}</text>`
  }).join('\n')
  return `<text class="group-title" x="${g.x}" y="337">${esc(g.heading)}</text>
    <path class="association" d="M${g.x} 206V282H${g.bus}V1000"/>
    ${cases}`
}

const svg = `<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="1800" height="1170" viewBox="0 0 1800 1170" role="img" aria-labelledby="title desc">
  <title id="title">个人博客系统用例图</title>
  <desc id="desc">游客、普通用户和管理员逐级继承权限，每类角色分别连接八项用例。</desc>
  <style>
    text { font-family: "Microsoft YaHei", "Noto Sans CJK SC", "PingFang SC", sans-serif; fill: #152b40; }
    .caption { font-size: 28px; font-weight: 700; text-anchor: middle; }
    .subtitle { font-size: 17px; fill: #5a6b7d; text-anchor: middle; }
    .boundary { fill: #ffffff; stroke: #344b61; stroke-width: 2.5; }
    .boundary-label { font-size: 23px; font-weight: 700; fill: #253d54; }
    .divider { stroke: #d9e2ea; stroke-width: 2; stroke-dasharray: 7 8; }
    .group-title { font-size: 21px; font-weight: 700; text-anchor: middle; fill: #264766; }
    .usecase { fill: #f5f9fc; stroke: #52718e; stroke-width: 2; }
    .usecase-label { font-size: 20px; dominant-baseline: middle; text-anchor: middle; }
    .association { fill: none; stroke: #66819a; stroke-width: 2; stroke-linejoin: round; }
    .actor { fill: none; stroke: #22394f; stroke-width: 3; stroke-linecap: round; stroke-linejoin: round; }
    .actor-label { fill: #152b40; stroke: #ffffff; stroke-width: 8; paint-order: stroke; font-size: 22px; font-weight: 700; text-anchor: middle; }
    .inheritance { fill: none; stroke: #344b61; stroke-width: 2.5; }
    .inheritance-label { font-size: 16px; text-anchor: middle; fill: #5a6b7d; }
  </style>
  <defs>
    <marker id="generalization" viewBox="0 0 20 20" refX="18" refY="10" markerWidth="16" markerHeight="16" orient="auto">
      <path d="M18 10L2 2V18Z" fill="#ffffff" stroke="#344b61" stroke-width="2"/>
    </marker>
  </defs>
  <rect width="1800" height="1170" fill="#ffffff"/>
  <text class="caption" x="900" y="47">图 2-1 个人博客系统用例图</text>
  <text class="subtitle" x="900" y="79">角色权限逐级扩展：游客 → 普通用户 → 管理员</text>
  <path class="inheritance" d="M852 119H420" marker-end="url(#generalization)"/>
  <path class="inheritance" d="M1382 119H950" marker-end="url(#generalization)"/>
  ${groups.map(g => actor(g.x, g.actor)).join('\n')}
  <rect class="boundary" x="95" y="265" width="1610" height="815" rx="12"/>
  <text class="boundary-label" x="420" y="300">个人博客系统</text>
  <path class="divider" d="M635 318V1054 M1165 318V1054"/>
  ${groups.map(group).join('\n')}
</svg>`

fs.writeFileSync(svgPath, svg, 'utf8')
sharp(Buffer.from(svg)).png({ compressionLevel: 9 }).toFile(pngPath)
  .then(() => console.log(`Generated ${svgPath} and ${pngPath}`))
  .catch(error => { console.error(error); process.exitCode = 1 })
