(function(){
'use strict';
const h = React.createElement;
const KEY = {pcstart:'pcstart-demo-key',buildlab:'buildlab-demo-key'};
const LABELS = {CPU:'Procesorius',MOTHERBOARD:'Pagrindinė plokštė',RAM:'Operatyvioji atmintis',GPU:'Vaizdo plokštė',STORAGE:'SSD saugykla',PSU:'Maitinimo šaltinis',CASE:'Korpusas'};
function field(title, value, update, options, hint) {
  return h('label',{className:'field',key:title},title,
    options ? h('select',{value:value,onChange:e=>update(e.target.value)},options.map(o=>h('option',{value:o[0],key:o[0]},o[1]))) :
    h('input',{type:'number',min:1,value:value,onChange:e=>update(e.target.value)}),
    hint ? h('span',{className:'hint'},hint) : null);
}
function priceLabel(p){return p&&p.priceEur!=null ? p.priceEur+' €' : '—';}
class App extends React.Component {
  constructor(props){super(props);this.state={tenant:'pcstart',catalog:[],builds:[],budget:'1000',purpose:'gaming',preferred:'',owned:'',label:'Mano kompiuteris',busy:false,result:null,error:'',demo:''};}
  componentDidMount(){this.load();}
  headers(){return {'X-Demo-Key':KEY[this.state.tenant],'Content-Type':'application/json'};}
  async request(path,method='GET',data){
    const res=await fetch('/api'+path,{method:method,headers:this.headers(),body:data?JSON.stringify(data):undefined});
    let json={};try{json=await res.json();}catch(_){}
    if(!res.ok)throw new Error((json.message||json.detail||json.error||'Užklausa nepavyko')+' (HTTP '+res.status+')');
    return json;
  }
  async load(){try{const [catalog,builds]=await Promise.all([this.request('/catalog'),this.request('/builds')]);this.setState({catalog,builds,error:''});}catch(e){this.setState({error:e.message});}}
  chooseTenant(tenant){this.setState({tenant,preferred:'',owned:'',result:null,demo:'',catalog:[],builds:[],error:''},()=>this.load());}
  async create(e){e.preventDefault();this.setState({busy:true,error:'',demo:''});try{
    const result=await this.request('/builds','POST',{budgetEur:Number(this.state.budget),purpose:this.state.purpose,
      preferredPartId:this.state.preferred||null,ownedPartIds:this.state.owned?[this.state.owned]:[],label:this.state.label});
    this.setState({result});this.load();
  }catch(ex){this.setState({error:ex.message,result:null});}finally{this.setState({busy:false});}}
  async showSaved(id){try{const result=await this.request('/builds/'+encodeURIComponent(id));this.setState({result,error:''});}catch(e){this.setState({error:e.message});}}
  async isolation(){
    const other=this.state.tenant==='pcstart'?'buildlab':'pcstart';const id='demo-'+other+'-001';let checks=[];
    for(const method of ['GET','PATCH']){
      try{await this.request('/builds/'+id,method,method==='PATCH'?{label:'Bandymas pakeisti svetimą įrašą'}:undefined);checks.push(method+': KLAIDA – gauta prieiga!');}
      catch(err){checks.push(method+': '+(err.message.includes('404')?'UŽBLOKUOTA, HTTP 404 ✓':err.message));}
    }
    this.setState({demo:checks.join('\n')});
  }
  renderResult(){const r=this.state.result;if(!r)return h('div',{className:'placeholder'},h('div',{className:'bubble'},'🖥'),
      h('strong',null,'Komplektacija dar nesudaryta'),h('p',null,'Nurodyk biudžetą, paskirtį ir paspausk „Parinkti komplektaciją“.'));
    return h('div',null,
      h('div',{className:'resultHeader'},h('h2',{style:{margin:0}},r.label),h('span',{className:'status'},'✓ Patikrinti pagrindiniai kriterijai')),
      h('div',{className:'stats'},
        h('div',{className:'stat'},h('b',null,r.plan.totalEur+' €'),h('span',null,'Mokėtina suma')),
        h('div',{className:'stat'},h('b',null,r.plan.score+'/100'),h('span',null,'Paskirties balas')),
        h('div',{className:'stat'},h('b',null,r.plan.parts.length),h('span',null,'Komponentai'))),
      h('table',{className:'parts'},h('thead',null,h('tr',null,h('th',null,'Tipas'),h('th',null,'Modelis'),h('th',null,'Kaina'))),
        h('tbody',null,r.plan.parts.map(p=>h('tr',{key:p.id},h('td',{className:'partType'},LABELS[p.type]||p.type),h('td',null,p.sourceUrl ? h('a',{href:p.sourceUrl,target:'_blank',rel:'noopener noreferrer',className:'part-link',title:'Atidaryti kainos šaltinį'},p.name) : p.name),h('td',null,(this.state.owned===p.id?'Turimas':priceLabel(p))))))),
      h('p',{className:'price-stamp'},'Komponentų kainų išrašas: 2026-11-16 (suapvalinta iki 1 €). Modelio pavadinimą spausk, kad atvertum kainos šaltinį.'),
      h('div',{className:'line'}),
      r.plan.notes.map((text,i)=>h('div',{className:'note'+(r.plan.replacedPartId&&i===0?' warning':''),key:i},text)),
      h('div',{style:{marginTop:18}},h('div',{className:'ai'},
        h('small',null,r.aiUsed?'AI paaiškinimas (patikrintas)':'Komplektacijos paaiškinimas'),
        r.explanation)),
      null);
  }
  render(){const s=this.state;let ownedOptions=[['','Neturiu arba nenaudosiu']],preferredOptions=[['','Nesvarbu – tegul sistema parenka']];
    for(const p of s.catalog){preferredOptions.push([p.id,(LABELS[p.type]||p.type)+' – '+p.name+' ('+priceLabel(p)+')']);
      if(p.type==='STORAGE')ownedOptions.push([p.id,p.name]);}
    return h('div',null,
      h('div',{className:'topbar'},h('div',{className:'brand'},'PC',h('span',null,'Builder')),
        null),
      h('div',{className:'shell'},
        h('div',{className:'intro'},h('div',null,h('div',{className:'eyebrow'},'Surenkamas kompiuteris be spėliojimo'),
          h('h1',null,'Tavo kompiuteris. Tavo biudžetas.'),
          h('p',null,'Pasirink paskirtį ir biudžetą. Sistema apskaičiuos suderinamą komplektaciją, o jei norima dalis netiks, pasiūlys patikrintą alternatyvą.')),
          null),
        h('div',{className:'layout'},
          h('div',null,h('section',{className:'panel'},h('h2',null,'1. Tavo poreikiai'),
            h('form',{onSubmit:e=>this.create(e)},
              field('Organizacijos katalogas',s.tenant,v=>this.chooseTenant(v),[['pcstart','PCStart'],['buildlab','BuildLab']],
                'Kiekviena organizacija turi atskirą duomenų katalogą ir komplektacijas.'),
              field('Biudžetas (€)',s.budget,v=>this.setState({budget:v}),null,'Pvz. 1000 €'),
              field('Kompiuterio paskirtis',s.purpose,v=>this.setState({purpose:v}),
                [['gaming','Žaidimams'],['office','Mokslams ir biurui'],['universal','Universaliam naudojimui']]),
              field('Pageidaujama dalis',s.preferred,v=>this.setState({preferred:v}),preferredOptions,
                'Jeigu dalis netiks, siūloma tos pačios kategorijos alternatyva.'),
              field('Jau turiu SSD (neprivaloma)',s.owned,v=>this.setState({owned:v}),ownedOptions,
                'Turimos SSD kainos neskaičiuojame į mokėtiną sumą.'),
              h('label',{className:'field'},'Komplektacijos pavadinimas',
                h('input',{type:'text',maxLength:80,value:s.label,onChange:e=>this.setState({label:e.target.value})})),
              h('button',{className:'primary',type:'submit',disabled:s.busy},s.busy?'Skaičiuojama...':'Parinkti komplektaciją')),
            null),
            h('section',{className:'panel demo'},h('h3',null,'Atsiskaitymo scenarijai'),
              h('p',null,'Paspaudus čia pakeičiami tik formos laukai. Norėdamas pademonstruoti rezultatą spausk „Parinkti komplektaciją“. '),
              h('div',{className:'flex'},
                h('button',{className:'secondary',onClick:()=>this.setState({budget:'1000',preferred:''})},'✓ 1000 €'),
                h('button',{className:'secondary',onClick:()=>this.setState({budget:'200',preferred:''})},'! 200 €'),
                s.tenant==='pcstart'&&h('button',{className:'secondary',onClick:()=>this.setState({budget:'1000',preferred:'a-gpu-5050'})},'↔ Per brangus pasirinkimas')),
              h('div',{className:'line'}),h('h3',null,'Klientų duomenų izoliavimas'),
              h('p',null,'Abu bandymai vykdomi serverio pusėje: GET (skaityti) ir PATCH (keisti) svetimą komplektaciją.'),
              h('button',{className:'secondary',onClick:()=>this.isolation()},'Patikrinti kito kliento apsaugą'),
              s.demo&&h('div',{className:'demo-output'},s.demo))),
          h('div',null,
            s.error&&h('div',{className:'error'},'⚠ '+s.error),
            h('section',{className:'panel'},this.renderResult()),
            h('section',{className:'panel demo'},h('h3',null,'2. Išsaugotos komplektacijos'),
              h('p',null,'Rodomos tik pasirinktos organizacijos komplektacijos.'),
              s.builds.length?s.builds.map(b=>h('div',{className:'saved',key:b.id},
                h('span',null,b.label+' · '+b.plan.totalEur+' €'),
                h('button',{onClick:()=>this.showSaved(b.id)},'Peržiūrėti'))):h('p',null,'Įrašų dar nėra.')))),
        null));
  }
}
ReactDOM.render(h(App),document.getElementById('root'));
})();
