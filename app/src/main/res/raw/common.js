(function(){
var verifyBtn = document.querySelector('#VerifyBtn');
var linkBtn = document.querySelector('#link-btn a');
var nextBtn = document.querySelector('#NextBtn');
var verifyInterval = null;
var nextInterval = null;

function verifyClick(){
verifyBtn.click();
if(verifyBtn.hidden === true){
clearInterval(verifyInterval);
}
    }

function nextClick() {
nextBtn.click();
if(nextBtn.hidden === true){
clearInterval(nextInterval);
}
    }

function callback(mutations, obs) {
  mutations.forEach(function(mutation) {
    if (mutation.type === 'attributes') {
      if (mutation.target.id === 'VerifyBtn' && mutation.target.hidden === false) {
            verifyBtn.click();
            var verifyInterval = setInterval(verifyClick, 3000);
      }
      else if (mutation.target.id ==='NextBtn' && mutation.target.hidden === false) {
            nextBtn.click();
            var nextInterval = setInterval(nextClick, 3000);
      }
      else if (linkBtn !== null && mutation.attributeName === 'class' && linkBtn.classList.value==='btn btn-primary rounded get-link xclude-popad' && linkBtn.innerHTML=="Get Link") {
              setTimeout(function() {
                  linkBtn.click();
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

JSBridge.showToast(verifyBtn.innerHTML);

var verifyBtnObserver = new MutationObserver(callback);
verifyBtnObserver.observe(verifyBtn, { attributes: true });
var nextBtnObserver = new MutationObserver(callback);
nextBtnObserver.observe(nextBtn, { attributes: true });

if(verifyBtn.hidden === false){
var verifyInterval = setInterval(verifyClick, 3000);
}

document.getElementById('myTimerDiv').style.display = 'none';
document.getElementById('myNextInst').style.display = 'block';
document.getElementById('VerifyBtn').style.display = 'block';

}

if(linkBtn !== null){
 var linkBtnObserver = new MutationObserver(callback);
 linkBtnObserver.observe(linkBtn, { attributes: true });

     linkBtn.addEventListener("click", function() {
        var JSBridgeVisited = document.querySelector('#JSBridgeVisited');
        if(JSBridgeVisited==undefined){
            var x = document.createElement("input");
            x.setAttribute("id", "JSBridgeVisited");
            x.setAttribute("type", "hidden");
            linkBtn.append(x);
            var get_link = document.querySelector('.get-link');
            get_link.click();
            JSBridge.setCompleted();
            var cookieNames = document.cookie.split(';').map(function(cookie) {
              return cookie.split('=')[0].trim();
            });
            // Loop through the cookie names and expire each one
            cookieNames.forEach(function(cookieName) {
              document.cookie = cookieName + '=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
            });

        }
     });
}



})();