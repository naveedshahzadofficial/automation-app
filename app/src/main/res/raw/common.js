(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
var linkBtn = document.querySelector('#link-btn a');
var nextBtn = document.querySelector('#NextBtn');
var closeStickyAdd = document.querySelector('#footer-sticky-ad .close-sticky-ad');
var verifyInterval = null;
var nextInterval = null;
var adsInterval = null;
var verifyClick=()=> {
verifyBtn.click();
if(verifyBtn.style.display === 'none'){
clearInterval(verifyInterval);
}
    };

var nextClick=()=>{
nextBtn.click();
adsInterval = setInterval(adsClick, 3000);
clearInterval(nextInterval);
if(nextBtn.style.display === 'none'){
clearInterval(nextInterval);
}
    };

var adsClick=()=>{
JSBridge.popUpClick();
};

function callback(mutations, obs) {
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes') {
      if (mutation.target.id === 'VerifyBtn' && mutation.target.style.display !== 'none') {
            closeStickyAdd.click();
            var yScroll = Math.floor(Math.random() * (1000 - 500 + 1) + 500);
            JSBridge.scrollToContinue(0, yScroll);
            //verifyBtn.scrollIntoView({ behavior: "smooth", block: 'center', inline: 'center' });
            verifyInterval = setInterval(verifyClick, 3000);
      }
      else if (mutation.target.id ==='NextBtn' && mutation.target.style.display !== 'none') {
        var isPopUp = document.querySelector('#PopUpChecked');
         if(isPopUp==undefined){
            var x = document.createElement("input");
            x.setAttribute("id", "PopUpChecked");
            x.setAttribute("type", "hidden");
            nextBtn.append(x);
            var yScroll = Math.floor(Math.random() * (12000 - 10000 + 1) + 10000);
            JSBridge.scrollToContinue(0, yScroll);
            //nextBtn.scrollIntoView({ behavior: "smooth", block: 'center', inline: 'center'  });
            nextInterval = setInterval(nextClick, 3000);
         }
      }
      else if (linkBtn !== null && mutation.attributeName === 'class' && linkBtn.classList.value==='btn btn-primary rounded get-link xclude-popad' && linkBtn.innerHTML=="Get Link") {
              setTimeout(function() {
                  //linkBtn.click();
                  completeGPLink();
              }, 3000);
       }
    }
  });
}


if(verifyBtn===null && linkBtn===null && nextBtn === null){
    let observer = new MutationObserver((mutations) => {
          mutations.forEach((mutation) => {
            if (!mutation.addedNodes) return

            for (let i = 0; i < mutation.addedNodes.length; i++) {
              let node = mutation.addedNodes[i]
              if(node.tagName === "INPUT"){
                const parentNode = node.parentNode;

                setTimeout(function() {
                JSBridge.verifyHuman();
                            }, 10000);
              }
            }
          })
        })
    observer.observe(document.body, {
          childList: true,
          subtree: true,
          attributes: false,
          characterData: false,
        })
}

if(verifyBtn !== null){

JSBridge.showToast("Processing...");

var verifyBtnObserver = new MutationObserver(callback);
verifyBtnObserver.observe(verifyBtn, { attributes: true });
var nextBtnObserver = new MutationObserver(callback);
nextBtnObserver.observe(nextBtn, { attributes: true });

if(verifyBtn.style.display !== 'none'){
  verifyInterval = setInterval(verifyClick, 1000);
}
}

if(linkBtn !== null){
 var linkBtnObserver = new MutationObserver(callback);
 linkBtnObserver.observe(linkBtn, { attributes: true });

     function completeGPLink(){
        var JSBridgeVisited = document.querySelector('#JSBridgeVisited');
        if(JSBridgeVisited==undefined){
            var x = document.createElement("input");
            x.setAttribute("id", "JSBridgeVisited");
            x.setAttribute("type", "hidden");
            linkBtn.append(x);
            JSBridge.setCompleted();
            var cookieNames = document.cookie.split(';').map(function(cookie) {
              return cookie.split('=')[0].trim();
            });
            // Loop through the cookie names and expire each one
            cookieNames.forEach(function(cookieName) {
              document.cookie = cookieName + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
            });

        }
     }
}



})();